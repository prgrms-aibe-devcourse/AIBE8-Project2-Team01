// 찜한 고수 (W·P15) · 고객·고수 · 20명씩 페이지 이동
// 응답의 별점·리뷰 수는 MVP 제외 항목이라 표시하지 않는다
import { useState } from 'react';
import { getBookmarks, removeBookmark, type BookmarkSummary } from '../api/mvp';
import { Empty, LoadBox, PageHead, Pager } from '../components/common';
import { useToast } from '../components/toast';
import { Avatar, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { friendly } from '../lib/messages';

export function bookmarkRegions(b: Pick<BookmarkSummary, 'nationwide' | 'wholeRegions' | 'regions'>): string {
  if (b.nationwide) return '전국';
  return [...b.wholeRegions.map((w) => `${w.firstRegionName} 전체`), ...b.regions.map((r) => `${r.firstRegionName} ${r.secondRegionName}`)].join(' · ');
}

export function BookmarksPage({ params }: { params: URLSearchParams }) {
  const toast = useToast();
  const page = Math.max(1, Number(params.get('page') ?? 1) || 1);
  const list = useLoad(() => getBookmarks(page), [page]);
  const [removing, setRemoving] = useState<number | null>(null);
  const { run } = useSingleFlight();

  const remove = (b: BookmarkSummary) => run(async () => {
    setRemoving(b.expertProfileId);
    try {
      await removeBookmark(b.expertProfileId);
      toast(`${b.expertNickname}님을 찜 목록에서 뺐어요.`);
      await list.reload(true).catch(() => undefined);
    } catch (e) { toast(`찜을 취소하지 못했어요.\n${friendly(e)}`, true); }
    finally { setRemoving(null); }
  });

  return (
    <main className="page narrow">
      <PageHead title="찜한 고수" back={{ href: '#/mypage', label: '마이페이지' }} sub="최근에 찜한 고수부터 보여요." />
      <LoadBox load={list.state} title="찜한 고수를 불러오지 못했어요" onRetry={() => list.reload()}>
        {(data) => data.content.length === 0 ? (
          page > 1 ? <Empty title="이 페이지에 찜한 고수가 없어요" action={<button type="button" className="btn" onClick={() => navigate('/bookmarks', { page: page - 1 })}>이전 페이지</button>} />
            : <Empty title="아직 찜한 고수가 없어요" desc={'고수 프로필에서 찜을 누르면 여기에 모여요.'} />
        ) : (
          <>
            <div className="card post-list">
              {data.content.map((b) => (
                <div className="bm-row" key={b.expertProfileId}>
                  <a className="row grow" href={`#/experts/${b.expertProfileId}`}>
                    <Avatar url={b.expertProfileImageUrl} size={52} />
                    <div className="col grow">
                      <b>{b.expertNickname}</b>
                      {b.category && <span className="badge teal self-start">대표 · {b.category.secondCategoryName}</span>}
                      <small className="sub ellipsis">{bookmarkRegions(b)}</small>
                    </div>
                  </a>
                  <button type="button" className="btn small like on" disabled={removing !== null} onClick={() => remove(b)} aria-label={`${b.expertNickname} 찜 취소`}>
                    {removing === b.expertProfileId ? <Spinner /> : '♥'} 찜함
                  </button>
                </div>
              ))}
            </div>
            <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => navigate('/bookmarks', { page: p })} />
            <small className="sub center">20명씩 · {data.pageNumber} / {Math.max(1, data.totalPages)} 페이지</small>
          </>
        )}
      </LoadBox>
    </main>
  );
}
