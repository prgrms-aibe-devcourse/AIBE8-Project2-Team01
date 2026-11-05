import { ChevronRight, Inbox } from 'lucide-react';
import { ApiError, errorText } from '../api/client';
import { getMatchedRequests, getServiceRequest } from '../api/mvp';
import { useLoad } from '../hooks/useLoad';
import { remainText, requestDeadline, shortDateTime } from '../lib/labels';
import CategoryIcon from './CategoryIcon';

// 고수 홈의 첫 섹션: 지금 들어온 요청서.
// 목록은 받은 요청서 API(GET /api/members/me/expert-profile/requests)를 그대로 쓰고 최신순으로 몇 건만 보여 준다.
// 목록 응답에는 요청 내용이 없어서 보여 주는 카드만 상세 API(GET /api/requests/{id})로 첫 답변을 가져와 요약한다.
const SHOW = 4;

async function loadLatest() {
  const list = await getMatchedRequests();
  const latest = [...list].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  const top = latest.slice(0, SHOW);
  const details = await Promise.all(top.map((r) => getServiceRequest(r.requestId).catch(() => null)));
  return {
    total: list.length,
    // summary: 첫 답변 내용 · 답변이 없으면 '' · 상세를 못 불러오면 null
    items: top.map((r, i) => ({ ...r, summary: details[i] ? details[i].answers?.find((a) => a.content?.trim())?.content ?? '' : null })),
  };
}

export default function ExpertHomeRequests() {
  const data = useLoad(loadLatest, []);
  const count = data.state.kind === 'ok' ? data.state.data.total : null;
  const noProfile = data.state.kind === 'error' && data.state.error instanceof ApiError && data.state.error.status === 404;

  return (
    <section className="section expert-home" aria-labelledby="incoming-title">
      <div className="container">
        <div className="section-head">
          <div className="title-row">
            <h2 id="incoming-title">지금 들어온 요청서</h2>
            {count !== null && <span className="incoming-count num">{count}건</span>}
          </div>
          <a className="more" href="#/received">
            전체 보기
            <ChevronRight size={16} aria-hidden="true" />
          </a>
        </div>
        <p className="expert-home-lead">내가 맡은 서비스와 활동 지역에 맞는 요청서예요. 마감 전에 견적을 보내 보세요.</p>

        {data.state.kind === 'loading' && (
          <ul className="incoming-grid" aria-hidden="true">
            {Array.from({ length: SHOW }, (_, i) => (
              <li key={i} className="incoming-card is-skeleton">
                <span className="sk-line" />
                <span className="sk-line short" />
              </li>
            ))}
          </ul>
        )}

        {data.state.kind === 'error' && (
          <div className="home-state">
            {noProfile ? (
              <>
                <p>고수 프로필이 없어서 요청서를 받을 수 없어요.</p>
                <a className="btn btn-secondary btn-sm" href="#/mypage">
                  마이페이지로
                </a>
              </>
            ) : (
              <>
                <p>요청서를 불러오지 못했어요. {errorText(data.state.error)}</p>
                <button type="button" className="btn btn-secondary btn-sm" onClick={() => data.reload()}>
                  다시 불러오기
                </button>
              </>
            )}
          </div>
        )}

        {data.state.kind === 'ok' && data.state.data.items.length === 0 && (
          <div className="home-state incoming-empty">
            <Inbox size={28} aria-hidden="true" />
            <p>
              <b>지금 들어온 요청서가 없어요</b>
              <br />
              고객이 내 서비스와 지역으로 요청서를 보내면 여기에 보여요.
            </p>
            <a className="btn btn-secondary btn-sm" href="#/expert/me">
              내 서비스·지역 확인
            </a>
          </div>
        )}

        {data.state.kind === 'ok' && data.state.data.items.length > 0 && (
          <ul className="incoming-grid">
            {data.state.data.items.map((r) => (
              <li key={r.requestId}>
                <a className="incoming-card" href={`#/received?id=${r.requestId}`}>
                  <span className="incoming-top">
                    <CategoryIcon secondName={r.secondCategoryName} size={48} />
                    {r.sentQuoteId ? (
                      <span className="badge teal">견적 보냄</span>
                    ) : (
                      <span className="badge blue">마감까지 {remainText(requestDeadline(r.createdAt))}</span>
                    )}
                  </span>
                  <b className="incoming-title">{r.secondCategoryName}</b>
                  <span className="incoming-meta">
                    {r.secondRegionName} · {shortDateTime(r.createdAt)}
                  </span>
                  <span className={`incoming-summary${r.summary ? '' : ' is-empty'}`}>
                    {r.summary === null ? '요청 내용은 상세에서 확인할 수 있어요.' : r.summary || '남긴 요청 내용이 없어요.'}
                  </span>
                </a>
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  );
}
