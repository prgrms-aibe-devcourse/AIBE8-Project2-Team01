// 보낸 견적 (W·11) · 고수 전용
// 매칭 대기(SENT)는 회수(삭제)할 수 있고 예약된 2크레딧이 해제된다. 매칭 완료(REQUESTED)는 삭제할 수 없다
import { useState } from 'react';
import { deleteQuote, getChatRooms, getMyQuotes, type Quote } from '../api/mvp';
import { Badge, Empty, LoadBox, PageHead } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, Lines, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { quoteBadge, quoteDeadline, shortDateTime, won } from '../lib/labels';
import { friendly } from '../lib/messages';

export function SentQuotesPage() {
  const toast = useToast();
  const list = useLoad(() => getMyQuotes(), []);
  const [tab, setTab] = useState<'SENT' | 'REQUESTED'>('SENT');
  const [target, setTarget] = useState<Quote | null>(null);
  const [err, setErr] = useState('');
  const del = useSingleFlight();
  const room = useSingleFlight();

  const withdraw = () => target && del.run(async () => {
    try {
      await deleteQuote(target.quoteId);
      setTarget(null);
      toast('견적을 회수했어요.\n예약된 2크레딧이 해제됐어요.');
      await list.reload(true).catch(() => undefined);
    } catch (e) {
      setErr(`회수하지 못했어요.\n${friendly(e)}`);
      list.reload(true).catch(() => undefined);
    }
  });

  // 매칭된 견적의 채팅방 찾기 (채팅 목록의 quoteId로 연결)
  const openChat = (quoteId: number) => room.run(async () => {
    try {
      for (let p = 1; p <= 10; p++) {
        const page = await getChatRooms(p);
        const r = page.content.find((c) => c.quoteId === quoteId);
        if (r) { navigate(`/chats/${r.roomId}`); return; }
        if (page.lastPage) break;
      }
      toast('채팅방을 찾지 못했어요.\n채팅 목록에서 확인해 주세요.', true);
    } catch (e) { toast(`채팅 목록을 불러오지 못했어요.\n${friendly(e)}`, true); }
  });

  return (
    <main className="page">
      <PageHead title="보낸 견적" />
      <LoadBox load={list.state} title="보낸 견적을 불러오지 못했어요" onRetry={() => list.reload()}>
        {(items) => {
          const sent = items.filter((q) => q.quoteStatus === 'SENT');
          const matched = items.filter((q) => q.quoteStatus === 'REQUESTED');
          const shown = tab === 'SENT' ? sent : matched;
          return (
            <div className="col gap">
              <div className="tabs">
                <button type="button" className={`tab${tab === 'SENT' ? ' on' : ''}`} onClick={() => setTab('SENT')}>매칭 대기 {sent.length}</button>
                <button type="button" className={`tab${tab === 'REQUESTED' ? ' on' : ''}`} onClick={() => setTab('REQUESTED')}>매칭 완료 {matched.length}</button>
              </div>
              {shown.length === 0 ? (
                <Empty title={tab === 'SENT' ? '매칭을 기다리는 견적이 없어요' : '아직 매칭된 견적이 없어요'}
                  desc={tab === 'SENT' ? '받은 요청서에 견적을 보내 보세요.\n유효기간(발송 후 3일)이 지난 견적은 목록에서 사라져요.' : '고객이 의뢰를 요청하면 여기에 표시되고 채팅이 열려요.'}
                  action={tab === 'SENT' ? <a className="btn primary" href="#/received">받은 요청서 보기</a> : undefined} />
              ) : (
                <div className="card table-card">
                  <table className="tbl stack-m sent-tbl">
                    <thead><tr><th>요청서</th><th>지역</th><th className="num-c">제안 가격</th><th>{tab === 'SENT' ? '유효기한' : '상태'}</th><th /></tr></thead>
                    <tbody>
                      {shown.map((q) => (
                        <tr key={q.quoteId}>
                          <td><b>{q.secondCategoryName}</b><br /><small className="sub">{shortDateTime(q.sentAt)} 발송</small></td>
                          <td>{q.secondRegionName}</td>
                          <td className="num-c"><b>{won(q.price)}</b></td>
                          <td>{q.quoteStatus === 'SENT' ? `${shortDateTime(quoteDeadline(q.sentAt).toISOString())}까지` : <Badge {...quoteBadge(q.quoteStatus)} />}</td>
                          <td className="act-c">
                            {q.quoteStatus === 'SENT'
                              ? <button type="button" className="btn small danger-ghost" onClick={() => { setErr(''); setTarget(q); }}>회수</button>
                              : <button type="button" className="btn small" disabled={room.busy} onClick={() => openChat(q.quoteId)}>채팅 열기</button>}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
              <Banner kind="info"><Lines text={'견적은 수정할 수 없어요.\n잘못 보냈다면 회수한 뒤 다시 작성해 주세요.\n매칭된 견적은 삭제할 수 없어요.'} /></Banner>
            </div>
          );
        }}
      </LoadBox>
      {target && (
        <Modal title="견적을 회수할까요?" onClose={() => !del.busy && setTarget(null)}
          actions={<>
            <button type="button" className="btn" disabled={del.busy} onClick={() => setTarget(null)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={del.busy} onClick={withdraw}>{del.busy ? <><Spinner light />회수하는 중</> : '회수'}</button>
          </>}>
          <p><Lines text={`${target.secondCategoryName} · ${won(target.price)}\n회수한 견적은 삭제되고 예약된 2크레딧이 해제돼요.\n필요하면 다시 작성해 보내 주세요.`} /></p>
          {err && <Banner><Lines text={err} /></Banner>}
        </Modal>
      )}
    </main>
  );
}
