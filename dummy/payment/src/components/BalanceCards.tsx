// 총잔액·예약·사용 가능 크레딧 카드
import type { MyCreditResponse } from '../types';

export function BalanceCards({ credit }: { credit: MyCreditResponse }) {
  return (
    <div className="balance-grid">
      <div className="card">
        <span className="label">총잔액</span>
        <span className="value">{credit.balance}</span>
        <span className="sub">내역 합계 · balance</span>
      </div>
      <div className="card">
        <span className="label">견적에 예약됨</span>
        <span className="value">{credit.reservedCredit}</span>
        <span className="sub">매칭 전 발송 견적 · 명세 미반영</span>
      </div>
      <div className="card strong">
        <span className="label">사용 가능</span>
        <span className="value">{credit.availableCredit}</span>
        <span className="sub">총잔액 − 예약 · 견적 {credit.availableQuoteCount}건 발송 가능</span>
      </div>
    </div>
  );
}
