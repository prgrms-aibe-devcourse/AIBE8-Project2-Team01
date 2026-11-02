// 화면 4: 충전·사용 내역 확인
import { useState } from 'react';
import { creditApi } from '../api/creditApi';
import { ApiPanel } from '../components/ApiPanel';
import { BalanceCards } from '../components/BalanceCards';
import { LinkButton } from '../components/LinkButton';
import { StepHeader } from '../components/StepHeader';
import { useDummy } from '../DummyContext';
import { useAsync } from '../hooks/useAsync';
import { formatDateTime, formatSigned, HISTORY_TYPE_LABEL } from '../lib/format';
import type { CreditHistoryType } from '../types';

const FILTERS: { value: CreditHistoryType | ''; label: string }[] = [
  { value: '', label: '전체' },
  { value: 'CHARGE', label: '충전' },
  { value: 'USE', label: '사용' },
  { value: 'REFUND', label: '환불' }
];

export function HistoryPage() {
  const { newPaymentIds } = useDummy();
  const [type, setType] = useState<CreditHistoryType | ''>('');
  const [page, setPage] = useState(1);
  const { data, error } = useAsync(
    () => Promise.all([creditApi.getMyCredit(), creditApi.getCreditHistories({ type, page }), creditApi.getReservations()]),
    [type, page]
  );

  if (error) return <p className="empty">{error}</p>;
  if (!data) return <p className="empty">불러오는 중…</p>;
  const [credit, histories, reservations] = data;

  const changeType = (value: CreditHistoryType | '') => {
    setType(value);
    setPage(1);
  };

  return (
    <>
      <StepHeader step={4} label="잔액·내역 확인" title="충전·사용 내역" />
      <BalanceCards credit={credit} />

      <section>
        <h2>내역</h2>
        <div className="filters" role="group" aria-label="유형 필터">
          {FILTERS.map((f) => (
            <button key={f.label} type="button" aria-pressed={type === f.value} onClick={() => changeType(f.value)}>
              {f.label}
            </button>
          ))}
        </div>
      </section>

      <div className="table-wrap">
        <table id="history-table">
          <thead>
            <tr>
              <th>일시</th>
              <th>유형</th>
              <th>관련</th>
              <th className="num">변동</th>
              <th className="num">변동 후 잔액</th>
            </tr>
          </thead>
          <tbody>
            {histories.content.length === 0 ? (
              <tr>
                <td colSpan={5} className="empty">
                  내역이 없습니다.
                </td>
              </tr>
            ) : (
              histories.content.map((h) => {
                const isNew = h.paymentId !== null && newPaymentIds.includes(h.paymentId);
                const related = h.paymentId !== null ? `결제 #${h.paymentId}` : h.quoteId !== null ? `견적 #${h.quoteId}` : '-';
                return (
                  <tr key={h.id} className={isNew ? 'new' : undefined}>
                    <td className="muted">{formatDateTime(h.createdAt)}</td>
                    <td>
                      {HISTORY_TYPE_LABEL[h.type]} {isNew && <span className="tag">방금 충전</span>}
                    </td>
                    <td>{related}</td>
                    <td className="num">{formatSigned(h.amount)}</td>
                    <td className="num">{h.balanceAfter}</td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      <div className="pager">
        <button type="button" className="btn small ghost" disabled={histories.pageNumber <= 1} onClick={() => setPage((p) => p - 1)}>
          이전
        </button>
        <span>
          {histories.pageNumber} / {histories.totalPages} 페이지 · 총 {histories.totalElements}건
        </span>
        <button type="button" className="btn small ghost" disabled={histories.lastPage} onClick={() => setPage((p) => p + 1)}>
          다음
        </button>
      </div>

      <section>
        <h2>
          견적에 예약된 크레딧 <span className="tag example">mock · 조회 API 미정</span>
        </h2>
        <p className="muted small">예약은 차감이 아니므로 위 내역(충전·사용·환불)에 포함되지 않습니다. 매칭되면 사용 내역으로 기록됩니다.</p>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>발송 일시</th>
                <th>견적</th>
                <th className="num">예약 크레딧</th>
              </tr>
            </thead>
            <tbody>
              {reservations.length === 0 ? (
                <tr>
                  <td colSpan={3} className="empty">
                    예약된 크레딧이 없습니다.
                  </td>
                </tr>
              ) : (
                reservations.map((r) => (
                  <tr key={r.quoteId}>
                    <td className="muted">{formatDateTime(r.sentAt)}</td>
                    <td>견적 #{r.quoteId}</td>
                    <td className="num">{r.credit}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>

      <div className="actions">
        <LinkButton href="#/credit" primary>
          크레딧 충전하기
        </LinkButton>
      </div>

      <ApiPanel
        points={[
          { method: 'GET', path: '/api/members/me/credit', id: 'API-66', status: 'draft', note: '잔액 카드. 예약·사용 가능 필드는 명세 추가 필요' },
          { method: 'GET', path: '/api/members/me/credit-histories?type=&page=&size=', id: 'API-67', status: 'draft', note: 'PageResponse. id·type·amount·balanceAfter·paymentId·quoteId·createdAt. size 기본값 미정' },
          { method: '?', path: '예약 중인 견적 목록', status: 'none', note: '예약 내역 표시 여부·API 미정' }
        ]}
        response={{ credit, histories, reservations }}
      />
    </>
  );
}
