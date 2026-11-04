// 채팅 목록 (W·05) · 웹은 목록과 대화방을 나란히 보여 준다
// 목록은 페이지 이동(20개씩), 대화방의 이전 메시지는 위로 스크롤해 불러온다
import { useCallback, useState } from 'react';
import { getChatRooms } from '../api/mvp';
import { Empty, LoadBox, Pager } from '../components/common';
import { Avatar, Lines } from '../components/ui';
import { useLoad } from '../hooks/useLoad';
import { smartTime } from '../lib/labels';
import { ChatRoomPanel } from './ChatRoomPage';

export function ChatListPage({ roomId }: { roomId?: number }) {
  const [page, setPage] = useState(1);
  const [onlyUnread, setOnlyUnread] = useState(false);
  const list = useLoad(() => getChatRooms(page), [page]);
  const { reload } = list;
  const refresh = useCallback(() => { reload(true).catch(() => undefined); }, [reload]);

  return (
    <main className={`page chat-page${roomId ? ' has-sel' : ''}`}>
      <div className="split chat-split">
        <aside className="split-list col gap-s">
          <div className="row between">
            <h1 className="page-title">채팅</h1>
            {list.state.kind === 'ok' && <small className="sub">안 읽음 {list.state.data.content.reduce((s, r) => s + (r.unreadCount > 0 ? 1 : 0), 0)}</small>}
          </div>
          <div className="tabs">
            <button type="button" className={`tab${!onlyUnread ? ' on' : ''}`} onClick={() => setOnlyUnread(false)}>전체</button>
            <button type="button" className={`tab${onlyUnread ? ' on' : ''}`} onClick={() => setOnlyUnread(true)}>안 읽음</button>
          </div>
          <LoadBox load={list.state} title="채팅 목록을 불러오지 못했어요" onRetry={() => list.reload()}>
            {(data) => {
              const rooms = data.content.filter((r) => !onlyUnread || r.unreadCount > 0);
              return (
                <>
                  {rooms.length === 0 ? (
                    <Empty title={onlyUnread ? '안 읽은 대화가 없어요' : '아직 채팅방이 없어요'} desc={onlyUnread ? undefined : '고객이 견적에 의뢰를 요청하면 채팅방이 열려요.'} />
                  ) : (
                    <div className="card chat-list">
                      {rooms.map((r) => (
                        <a key={r.roomId} href={`#/chats/${r.roomId}`} className={`chat-item${r.roomId === roomId ? ' on' : ''}`}>
                          <Avatar url={r.partner?.profileImageUrl} size={44} />
                          <div className="col grow">
                            <span className="row between"><b className="ellipsis">{r.partner?.nickname ?? '알 수 없는 사용자'}</b><small className="sub">{smartTime(r.lastMessageAt ?? r.createdAt)}</small></span>
                            <span className="row between">
                              <span className="sub ellipsis">{r.partnerLeft ? '상대방이 대화를 나갔어요' : r.lastMessagePreview ?? '아직 메시지가 없어요'}</span>
                              {r.unreadCount > 0 && <span className="unread">{r.unreadCount > 99 ? '99+' : r.unreadCount}</span>}
                            </span>
                          </div>
                        </a>
                      ))}
                    </div>
                  )}
                  <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={setPage} />
                </>
              );
            }}
          </LoadBox>
          <small className="sub"><Lines text={'채팅방을 나가면 내 목록에서만 사라지고\n상대방의 대화는 그대로 남아요.'} /></small>
        </aside>
        <section className="split-detail">
          {roomId
            ? <ChatRoomPanel key={roomId} roomId={roomId} summary={list.state.kind === 'ok' ? list.state.data.content.find((r) => r.roomId === roomId) : undefined} onActivity={refresh} />
            : <div className="state card chat-empty"><b>대화를 선택해 주세요</b><span className="sub">의뢰를 요청한 고수와 일정·비용을 채팅으로 조율할 수 있어요.</span></div>}
        </section>
      </div>
    </main>
  );
}
