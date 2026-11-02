// 화면별 '필요 데이터·API 연결 지점' 패널 (결제 담당자 확인용, 실서비스 제외)

export interface ApiPoint {
  method: string;
  path: string;
  id?: string;
  /** draft: 노션 명세 초안·미구현, none: 대응 API 없음(미확정) */
  status: 'draft' | 'none';
  note: string;
}

export function ApiPanel({ points, response }: { points: ApiPoint[]; response?: unknown }) {
  return (
    <details className="api">
      <summary>이 화면의 데이터 · API 연결 지점</summary>
      <ul>
        {points.map((p) => (
          <li key={`${p.method} ${p.path}`}>
            <code>
              {p.method} {p.path}
            </code>{' '}
            {p.id}{' '}
            <span className={p.status === 'none' ? 'status-none' : 'status-draft'}>
              [{p.status === 'none' ? '대응 API 없음 · 미확정' : '명세 초안 · 미구현'}]
            </span>
            <br />
            <span className="muted">{p.note}</span>
          </li>
        ))}
      </ul>
      {response !== undefined && (
        <>
          <div className="muted">마지막 mock 응답</div>
          <pre>{JSON.stringify(response, null, 2)}</pre>
        </>
      )}
    </details>
  );
}
