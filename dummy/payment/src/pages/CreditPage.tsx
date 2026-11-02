// 화면 1: 크레딧 잔액 확인 + 충전 상품 선택
import { useState } from 'react';
import { creditApi } from '../api/creditApi';
import { ApiPanel } from '../components/ApiPanel';
import { BalanceCards } from '../components/BalanceCards';
import { LinkButton } from '../components/LinkButton';
import { StepHeader } from '../components/StepHeader';
import { useAsync } from '../hooks/useAsync';
import { navigate } from '../hooks/useHashRoute';
import { formatWon } from '../lib/format';
import { CREDIT_PER_QUOTE, CREDIT_UNIT_PRICE } from '../lib/policy';

export function CreditPage() {
  const { data, error } = useAsync(() => Promise.all([creditApi.getMyCredit(), creditApi.getCreditProducts()]), []);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  if (error) return <p className="empty">{error}</p>;
  if (!data) return <p className="empty">불러오는 중…</p>;
  const [credit, products] = data;

  return (
    <>
      <StepHeader step={1} label="크레딧 확인·상품 선택" title="크레딧 충전" />
      <BalanceCards credit={credit} />
      {credit.availableCredit < CREDIT_PER_QUOTE && (
        <p className="notice">사용 가능 크레딧이 {CREDIT_PER_QUOTE}보다 적어 견적을 보낼 수 없습니다.</p>
      )}
      <p className="notice">
        견적 1건을 보내면 {CREDIT_PER_QUOTE}크레딧이 예약되고, 고객이 의뢰를 요청해 매칭되면 실제로 차감됩니다. 1크레딧 ={' '}
        {formatWon(CREDIT_UNIT_PRICE)}
      </p>

      <section>
        <h2>충전 상품</h2>
        {products.length === 0 ? (
          <p className="empty">판매 중인 충전 상품이 없습니다.</p>
        ) : (
          <div className="products" role="radiogroup" aria-label="충전 상품">
            {products.map((p) => (
              <button
                key={p.id}
                type="button"
                role="radio"
                aria-checked={p.id === selectedId}
                className="product"
                data-product={p.id}
                onClick={() => setSelectedId(p.id)}
              >
                <span className="name">{p.name}</span>
                <span className="price">{formatWon(p.price)}</span>
                <span className="muted small">
                  충전 {p.creditAmount}크레딧 · 견적 {Math.floor(p.creditAmount / CREDIT_PER_QUOTE)}건분
                </span>
                <span className={`tag${p.isExample ? ' example' : ''}`}>{p.isExample ? '예시 데이터' : 'API 명세 예시 값'}</span>
              </button>
            ))}
          </div>
        )}
        <p className="muted small">상품 구간은 문서·코드에 확정 값이 없어 예시로 구성했습니다. 가격 = 크레딧 × 1,000원 규칙만 확정입니다.</p>
      </section>

      <div className="actions">
        <button
          type="button"
          className="btn primary"
          id="btn-next"
          disabled={selectedId === null}
          onClick={() => selectedId !== null && navigate('/checkout', { productId: selectedId })}
        >
          다음: 결제 정보 확인
        </button>
        <LinkButton href="#/history">충전·사용 내역</LinkButton>
      </div>

      <ApiPanel
        points={[
          { method: 'GET', path: '/api/members/me/credit', id: 'API-66', status: 'draft', note: 'balance·creditPerQuote·availableQuoteCount. 예약·사용 가능 크레딧 필드는 명세에 없음' },
          { method: 'GET', path: '/api/credit-products', id: 'API-65', status: 'draft', note: 'id·name·price·creditAmount·bonusCredit(MVP는 0). 활성 상품 가격 오름차순' }
        ]}
        response={{ credit, products }}
      />
    </>
  );
}
