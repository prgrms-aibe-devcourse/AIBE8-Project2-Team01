// 최소 STOMP 1.2 클라이언트 (패키지를 추가하지 않고 브라우저 WebSocket 위에서 프레임만 처리)
// 백엔드 계약: 엔드포인트 /ws · CONNECT 프레임의 Authorization 헤더로 인증
//   구독 /sub/chat-rooms/{roomId} · 전송 /pub/chat-rooms/{roomId}/messages · 본인 오류 /user/queue/errors
const NUL = '\u0000';

export interface StompFrame { command: string; headers: Record<string, string>; body: string }

function wsUrl(): string {
  const base = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '');
  if (base) return base.replace(/^http/, 'ws') + '/ws';
  return `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/ws`;
}

function encode(command: string, headers: Record<string, string>, body = ''): string {
  const h = Object.entries(headers).map(([k, v]) => `${k}:${v}`).join('\n');
  return `${command}\n${h}${h ? '\n' : ''}\n${body}${NUL}`;
}

function decode(raw: string): StompFrame | null {
  const text = raw.replace(/^\n+/, '');
  if (!text) return null; // 하트비트
  const end = text.indexOf('\n\n');
  const head = end === -1 ? text : text.slice(0, end);
  const body = end === -1 ? '' : text.slice(end + 2).replace(/\u0000$/, '');
  const [command, ...lines] = head.split('\n');
  const headers: Record<string, string> = {};
  for (const l of lines) { const i = l.indexOf(':'); if (i > 0) headers[l.slice(0, i)] = l.slice(i + 1); }
  return { command: command.trim(), headers, body };
}

export interface StompHandlers {
  onConnected: () => void;
  onMessage: (destination: string, body: string) => void;
  onError: (message: string) => void;        // ERROR 프레임 (인증 실패·권한 없음 등)
  onClose: () => void;                       // 연결이 끊김
}

export class StompClient {
  private ws: WebSocket | null = null;
  private buffer = '';
  private subId = 0;
  private beat: number | null = null;
  private closedByUser = false;

  constructor(private token: string, private h: StompHandlers) {}

  connect() {
    this.closedByUser = false;
    const ws = new WebSocket(wsUrl());
    this.ws = ws;
    ws.onopen = () => ws.send(encode('CONNECT', { 'accept-version': '1.2', host: window.location.hostname, 'heart-beat': '0,20000', Authorization: `Bearer ${this.token}` }));
    ws.onmessage = (ev) => {
      this.buffer += typeof ev.data === 'string' ? ev.data : '';
      let i;
      while ((i = this.buffer.indexOf(NUL)) !== -1) {
        const frame = decode(this.buffer.slice(0, i));
        this.buffer = this.buffer.slice(i + 1);
        if (!frame) continue;
        if (frame.command === 'CONNECTED') this.h.onConnected();
        else if (frame.command === 'MESSAGE') this.h.onMessage(frame.headers.destination ?? '', frame.body);
        else if (frame.command === 'ERROR') this.h.onError(frame.headers.message || frame.body || '연결 오류');
      }
    };
    ws.onclose = () => { this.stopBeat(); if (!this.closedByUser) this.h.onClose(); };
    ws.onerror = () => { /* onclose에서 처리 */ };
    // 서버가 끊김을 감지할 수 있도록 20초마다 빈 줄을 보낸다
    this.beat = window.setInterval(() => { if (this.ws?.readyState === WebSocket.OPEN) this.ws.send('\n'); }, 20000);
  }

  subscribe(destination: string) {
    this.ws?.send(encode('SUBSCRIBE', { id: `sub-${++this.subId}`, destination }));
  }

  send(destination: string, body: unknown): boolean {
    if (this.ws?.readyState !== WebSocket.OPEN) return false;
    this.ws.send(encode('SEND', { destination, 'content-type': 'application/json' }, JSON.stringify(body)));
    return true;
  }

  get open() { return this.ws?.readyState === WebSocket.OPEN; }

  close() {
    this.closedByUser = true;
    this.stopBeat();
    try { if (this.ws?.readyState === WebSocket.OPEN) this.ws.send(encode('DISCONNECT', {})); } catch { /* 무시 */ }
    this.ws?.close();
    this.ws = null;
  }

  private stopBeat() { if (this.beat !== null) { clearInterval(this.beat); this.beat = null; } }
}
