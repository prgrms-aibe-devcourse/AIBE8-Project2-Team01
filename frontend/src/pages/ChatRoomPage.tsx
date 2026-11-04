// 채팅방 (W·06) · 텍스트 송수신 · 이전 메시지 스크롤 조회 · 읽음 · 나가기
// 메시지 삭제 API가 없어 삭제는 제공하지 않는다. 이미지 전송은 MVP 후순위(CHAT-03)라 넣지 않았다
import { useCallback, useEffect, useLayoutEffect, useRef, useState, type FormEvent, type KeyboardEvent } from 'react';
import { ApiError, errorText, refreshAccessToken, tokenStore } from '../api/client';
import { getChatMessages, getQuote, leaveChatRoom, readChatRoom, type ChatMessage, type ChatRoomSummary, type Quote } from '../api/mvp';
import { useSession } from '../auth/session';
import { useToast } from '../components/toast';
import { Avatar, Banner, ErrorView, Lines, LoadingView, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { dotDate, timeOnly, won } from '../lib/labels';
import { friendly } from '../lib/messages';
import { StompClient } from '../lib/stomp';

const MAX = 500;
type Conn = 'connecting' | 'open' | 'retrying' | 'failed';

export function ChatRoomPanel({ roomId, summary, onActivity }: { roomId: number; summary?: ChatRoomSummary; onActivity: () => void }) {
  const { me } = useSession();
  const toast = useToast();
  const [msgs, setMsgs] = useState<ChatMessage[]>([]);
  const [state, setState] = useState<'loading' | 'ok' | 'error' | 'forbidden'>('loading');
  const [loadErr, setLoadErr] = useState<unknown>(null);
  const [cursor, setCursor] = useState<number | null>(null);
  const [hasNext, setHasNext] = useState(false);
  const [older, setOlder] = useState<'idle' | 'loading' | 'error'>('idle');
  const [quote, setQuote] = useState<Quote | null>(null);
  const [text, setText] = useState('');
  const [sending, setSending] = useState(false);
  const [sendErr, setSendErr] = useState('');
  const [conn, setConn] = useState<Conn>('connecting');
  const [askLeave, setAskLeave] = useState(false);
  const leave = useSingleFlight();
  const listRef = useRef<HTMLDivElement>(null);
  const stomp = useRef<StompClient | null>(null);
  const keepBottom = useRef(true);
  const prevHeight = useRef<number | null>(null);
  const pendingTimer = useRef<number | null>(null);
  const retries = useRef(0);
  const partnerLeft = summary?.partnerLeft ?? false;

  // 최신 메시지 30개 (처음 열 때·재연결 후 놓친 메시지 보충)
  const loadLatest = useCallback(async (merge: boolean) => {
    const res = await getChatMessages(roomId);
    const latest = [...res.messages].reverse();
    setMsgs((cur) => {
      if (!merge) return latest;
      const ids = new Set(cur.map((m) => m.messageId));
      return [...cur, ...latest.filter((m) => !ids.has(m.messageId))].sort((a, b) => a.messageId - b.messageId);
    });
    if (!merge) { setCursor(res.nextCursor); setHasNext(res.hasNext); }
    keepBottom.current = true;
  }, [roomId]);

  // 목록 갱신 콜백은 렌더마다 바뀔 수 있어 ref로 고정한다 (조회·연결이 다시 실행되지 않게)
  const activityRef = useRef(onActivity);
  activityRef.current = onActivity;
  const markRead = useCallback(() => { readChatRoom(roomId).then(() => activityRef.current()).catch(() => undefined); }, [roomId]);

  useEffect(() => {
    setState('loading');
    loadLatest(false).then(() => { setState('ok'); markRead(); }).catch((e) => {
      setLoadErr(e);
      setState(e instanceof ApiError && e.status === 403 ? 'forbidden' : 'error');
    });
  }, [loadLatest, markRead]);

  useEffect(() => {
    if (!summary?.quoteId) return;
    getQuote(summary.quoteId).then(setQuote).catch(() => setQuote(null));
  }, [summary?.quoteId]);

  // 웹소켓 연결: 끊기면 다시 연결하고 놓친 메시지를 불러온다
  const connect = useCallback(() => {
    const token = tokenStore.get();
    if (!token) return;
    stomp.current?.close();
    setConn(retries.current ? 'retrying' : 'connecting');
    const c = new StompClient(token, {
      onConnected: () => {
        c.subscribe(`/sub/chat-rooms/${roomId}`);
        c.subscribe('/user/queue/errors');
        if (retries.current) loadLatest(true).catch(() => undefined);
        retries.current = 0;
        setConn('open');
      },
      onMessage: (dest, body) => {
        try {
          const data = JSON.parse(body);
          if (dest.endsWith('/queue/errors')) {
            clearPending();
            setSending(false);
            setSendErr(`메시지를 보내지 못했어요.\n${friendly(new ApiError(400, data.message ?? ''))}`);
            return;
          }
          const m = data as ChatMessage;
          setMsgs((cur) => cur.some((x) => x.messageId === m.messageId) ? cur : [...cur, m]);
          const el = listRef.current;
          keepBottom.current = !el || el.scrollHeight - el.scrollTop - el.clientHeight < 120 || m.senderId === me?.id;
          if (m.senderId === me?.id) { clearPending(); setSending(false); setText(''); }
          else markRead();
        } catch { /* 형식이 다른 프레임은 무시 */ }
      },
      onError: async (msg) => {
        // 인증 실패면 토큰을 재발급해 한 번 더 연결한다
        if (/인증/.test(msg) && retries.current < 1 && await refreshAccessToken()) { retries.current++; connect(); return; }
        setConn('failed');
      },
      onClose: () => {
        if (retries.current >= 5) { setConn('failed'); return; }
        retries.current++;
        setConn('retrying');
        window.setTimeout(connect, Math.min(8000, 1000 * 2 ** retries.current));
      }
    });
    stomp.current = c;
    c.connect();
  }, [roomId, loadLatest, markRead, me?.id]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (state !== 'ok' || partnerLeft) return;
    connect();
    return () => { stomp.current?.close(); stomp.current = null; };
  }, [state, partnerLeft, connect]);

  const clearPending = () => { if (pendingTimer.current) { clearTimeout(pendingTimer.current); pendingTimer.current = null; } };
  useEffect(() => clearPending, []);

  // 스크롤 위치: 새 메시지는 아래로, 이전 메시지를 붙이면 보던 위치 유지
  useLayoutEffect(() => {
    const el = listRef.current; if (!el) return;
    if (prevHeight.current !== null) { el.scrollTop = el.scrollHeight - prevHeight.current; prevHeight.current = null; return; }
    if (keepBottom.current) el.scrollTop = el.scrollHeight;
  }, [msgs]);

  const loadOlder = useCallback(async () => {
    if (!hasNext || older === 'loading' || cursor === null) return;
    setOlder('loading');
    try {
      const res = await getChatMessages(roomId, cursor);
      prevHeight.current = listRef.current?.scrollHeight ?? null;
      keepBottom.current = false;
      setMsgs((cur) => [...[...res.messages].reverse().filter((m) => !cur.some((c) => c.messageId === m.messageId)), ...cur]);
      setCursor(res.nextCursor); setHasNext(res.hasNext); setOlder('idle');
    } catch { setOlder('error'); }
  }, [hasNext, older, cursor, roomId]);

  const onScroll = () => { const el = listRef.current; if (el && el.scrollTop < 60) loadOlder(); };

  const send = (e?: FormEvent) => {
    e?.preventDefault();
    const content = text.trim();
    if (!content || sending) return;
    if (content.length > MAX) { setSendErr(`메시지는 ${MAX}자까지 보낼 수 있어요.`); return; }
    setSendErr('');
    if (!stomp.current?.send(`/pub/chat-rooms/${roomId}/messages`, { type: 'TEXT', content })) {
      setSendErr('연결이 끊겨 보내지 못했어요.\n다시 연결되면 보내 주세요.'); return;
    }
    setSending(true);
    // 서버에서 내 메시지가 돌아오면 입력창을 비운다. 오래 응답이 없으면 실패로 안내하고 입력값은 남긴다
    pendingTimer.current = window.setTimeout(() => { setSending(false); setSendErr('메시지 전송을 확인하지 못했어요.\n잠시 후 다시 보내 주세요.'); }, 8000);
  };
  const onKey = (e: KeyboardEvent<HTMLTextAreaElement>) => { if (e.key === 'Enter' && !e.shiftKey && !e.nativeEvent.isComposing) { e.preventDefault(); send(); } };

  const doLeave = () => leave.run(async () => {
    try {
      await leaveChatRoom(roomId);
      stomp.current?.close();
      toast('채팅방을 나갔어요.');
      activityRef.current();
      navigate('/chats', undefined, true);
    } catch (e) { toast(`나가지 못했어요.\n${friendly(e)}`, true); }
  });

  if (state === 'loading') return <div className="card chat-room"><LoadingView label="대화를 불러오는 중이에요" /></div>;
  if (state === 'forbidden') return <div className="state card"><b>볼 수 없는 채팅방이에요</b><span className="sub">참여하지 않았거나 이미 나간 채팅방이에요.</span><a className="btn" href="#/chats">채팅 목록으로</a></div>;
  if (state === 'error') return <ErrorView title="대화를 불러오지 못했어요" message={errorText(loadErr)} onRetry={() => { setState('loading'); loadLatest(false).then(() => setState('ok')).catch((e) => { setLoadErr(e); setState('error'); }); }} />;

  const partner = summary?.partner;
  let lastDay = '';
  return (
    <div className="card chat-room">
      <div className="chat-head">
        <a className="back show-m" href="#/chats">‹</a>
        <Avatar url={partner?.profileImageUrl} size={40} />
        <div className="col grow">
          <b className="ellipsis">{partner?.nickname ?? '대화 상대'}</b>
          {quote && <small className="sub">{quote.secondCategoryName} · {won(quote.price)} 견적</small>}
        </div>
        <button type="button" className="btn small" onClick={() => setAskLeave(true)}>나가기</button>
      </div>
      {conn === 'retrying' && <div className="chat-conn"><Lines text={'연결이 끊겼어요.\n다시 연결하는 중이에요.'} /></div>}
      {conn === 'failed' && <div className="chat-conn err">연결하지 못했어요. <button type="button" className="link-btn inline" onClick={() => { retries.current = 0; connect(); }}>다시 연결</button></div>}
      <div className="chat-body" ref={listRef} onScroll={onScroll}>
        {hasNext ? (
          <div className="chat-older">
            {older === 'loading' ? <><Spinner />이전 메시지를 불러오는 중이에요</> : older === 'error'
              ? <>이전 메시지를 불러오지 못했어요. <button type="button" className="link-btn inline" onClick={loadOlder}>다시 시도</button></>
              : <button type="button" className="link-btn inline" onClick={loadOlder}>이전 메시지 보기</button>}
          </div>
        ) : msgs.length > 0 && <div className="chat-older">첫 메시지예요</div>}
        {msgs.length === 0 && <div className="state"><b>대화를 시작해 보세요</b><span className="sub">일정과 비용을 채팅으로 조율할 수 있어요.</span></div>}
        {msgs.map((m) => {
          const day = dotDate(m.createdAt);
          const sep = day !== lastDay; lastDay = day;
          const mine = m.senderId === me?.id;
          return (
            <div key={m.messageId}>
              {sep && <div className="chat-day">{day}</div>}
              <div className={`msg${mine ? ' mine' : ''}`}>
                <div className="bubble">{m.type === 'TEXT' ? m.content : m.imageUrl ? <img src={m.imageUrl} alt="보낸 사진" className="msg-img" /> : '사진'}</div>
                <small className="msg-time">{timeOnly(m.createdAt)}</small>
              </div>
            </div>
          );
        })}
      </div>
      {partnerLeft ? (
        <div className="chat-closed">상대방이 나간 채팅방이에요.<br />메시지를 보낼 수 없어요.</div>
      ) : (
        <form className="chat-input" onSubmit={send}>
          {sendErr && <Banner><Lines text={sendErr} /></Banner>}
          <div className="row">
            <textarea className={`input chat-ta${text.length > MAX ? ' bad' : ''}`} value={text} placeholder="메시지를 입력해 주세요" rows={1}
              onChange={(e) => { setText(e.target.value); setSendErr(''); }} onKeyDown={onKey} disabled={sending} />
            <button type="submit" className="btn primary" disabled={!text.trim() || sending || conn !== 'open' || text.length > MAX}>{sending ? <Spinner light /> : '전송'}</button>
          </div>
          <small className={`sub right${text.length > MAX ? ' err-t' : ''}`}>{text.length}/{MAX}</small>
        </form>
      )}
      {askLeave && (
        <Modal title="채팅방을 나갈까요?" onClose={() => !leave.busy && setAskLeave(false)}
          actions={<>
            <button type="button" className="btn" disabled={leave.busy} onClick={() => setAskLeave(false)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={leave.busy} onClick={doLeave}>{leave.busy ? <><Spinner light />나가는 중</> : '나가기'}</button>
          </>}>
          <p><Lines text={'나가면 내 목록에서 사라지고 다시 들어올 수 없어요.\n상대방의 대화는 그대로 남지만 더 이상 메시지를 주고받을 수 없어요.'} /></p>
        </Modal>
      )}
    </div>
  );
}
