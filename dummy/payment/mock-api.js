// EFY 크레딧 충전 더미 - mock API 계층
// 실제 결제 API·운영 DB를 호출하지 않는다. 모든 데이터는 브라우저 메모리에만 있다.
// 함수 이름 옆의 [API-xx]는 노션 API 명세서의 초안 API이며 아직 구현되지 않았다.
(function (global) {
  'use strict';

  // 확정 정책 (MVP 요구사항 CR-01·CR-07)
  var CREDIT_UNIT_PRICE = 1000; // 1크레딧 = 1,000원
  var CREDIT_PER_QUOTE = 2;     // 견적 1건 발송 시 예약 / 매칭 시 차감

  // 응답 지연 흉내 (ms)
  var LATENCY = 500;

  // 초기 상태 생성 - 초기화 버튼에서 다시 호출한다
  function createInitialState() {
    return {
      // 충전 상품: 예시 데이터 (상품 구간은 문서·코드에 없음, Q-06 미확정)
      // 가격 = 지급량 × 1,000원 규칙만 확정 정책이다
      products: [
        { id: 1, name: '크레딧 10개', price: 10000, creditAmount: 10, bonusCredit: 0, isExample: true },
        { id: 2, name: '크레딧 30개', price: 30000, creditAmount: 30, bonusCredit: 0, isExample: true },
        { id: 3, name: '크레딧 50개', price: 50000, creditAmount: 50, bonusCredit: 0, isExample: true },
        // API-65 예시 응답의 이름·가격을 사용 (bonusCredit 10은 MVP 정책상 0으로 둔다)
        { id: 4, name: '크레딧 100개', price: 100000, creditAmount: 100, bonusCredit: 0, isExample: false }
      ],
      // 원장(내역). 잔액 = 내역 합계. 최신순 정렬은 조회 시 처리
      histories: [
        { id: 1, type: 'CHARGE', amount: 10, balanceAfter: 10, paymentId: 1, quoteId: null, createdAt: '2026-10-28T09:10:00' },
        { id: 2, type: 'USE', amount: -2, balanceAfter: 8, paymentId: null, quoteId: 21, createdAt: '2026-10-30T15:20:00' },
        { id: 3, type: 'CHARGE', amount: 10, balanceAfter: 18, paymentId: 2, quoteId: null, createdAt: '2026-10-31T11:02:00' },
        { id: 4, type: 'USE', amount: -2, balanceAfter: 16, paymentId: null, quoteId: 27, createdAt: '2026-11-01T14:20:00' }
      ],
      // 예약: 발송 후 매칭되지 않은 SENT 견적 (조회 API 미정, 화면 표시용 mock)
      reservations: [
        { quoteId: 34, credit: 2, sentAt: '2026-11-01T18:44:00' },
        { quoteId: 35, credit: 2, sentAt: '2026-11-02T08:30:00' }
      ],
      // 결제 주문 (서버의 Payment 엔티티 역할)
      payments: [
        { paymentId: 1, orderId: 'efy-mock-0001', productId: 1, orderName: '크레딧 10개', amount: 10000, creditAmount: 10, status: 'DONE', paymentKey: 'mock_pk_0001' },
        { paymentId: 2, orderId: 'efy-mock-0002', productId: 1, orderName: '크레딧 10개', amount: 10000, creditAmount: 10, status: 'DONE', paymentKey: 'mock_pk_0002' }
      ],
      seq: { payment: 2, history: 4 }
    };
  }

  var state = createInitialState();

  // 깊은 복사로 내부 상태를 화면에 직접 노출하지 않는다
  function clone(v) { return JSON.parse(JSON.stringify(v)); }

  function delay(value) {
    return new Promise(function (resolve) { setTimeout(function () { resolve(clone(value)); }, LATENCY); });
  }

  function fail(status, message) {
    return new Promise(function (_, reject) {
      setTimeout(function () { reject({ status: status, message: message }); }, LATENCY);
    });
  }

  function now() {
    // 로컬 시각을 ISO 형식(초 단위)으로
    var d = new Date();
    var p = function (n) { return String(n).padStart(2, '0'); };
    return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + 'T' + p(d.getHours()) + ':' + p(d.getMinutes()) + ':' + p(d.getSeconds());
  }

  function totalBalance() {
    return state.histories.reduce(function (sum, h) { return sum + h.amount; }, 0);
  }

  function reservedCredit() {
    return state.reservations.reduce(function (sum, r) { return sum + r.credit; }, 0);
  }

  function findPayment(orderId) {
    return state.payments.find(function (p) { return p.orderId === orderId; });
  }

  // 크레딧 지급 - 주문당 한 번만 실행된다 (CR-03 멱등성)
  function grantCredit(payment) {
    if (payment.status === 'DONE') return; // 이미 지급된 주문은 재지급하지 않는다
    payment.status = 'DONE';
    var balanceAfter = totalBalance() + payment.creditAmount;
    state.seq.history += 1;
    state.histories.push({
      id: state.seq.history, type: 'CHARGE', amount: payment.creditAmount, balanceAfter: balanceAfter,
      paymentId: payment.paymentId, quoteId: null, createdAt: now()
    });
  }

  function confirmResult(payment) {
    return { orderId: payment.orderId, paymentStatus: payment.status, chargedCredit: payment.creditAmount, balance: totalBalance() };
  }

  var api = {
    CREDIT_UNIT_PRICE: CREDIT_UNIT_PRICE,
    CREDIT_PER_QUOTE: CREDIT_PER_QUOTE,

    // [API-65] GET /api/credit-products - 활성 상품을 가격 오름차순으로
    getCreditProducts: function () {
      var list = state.products.slice().sort(function (a, b) { return a.price - b.price; });
      return delay(list);
    },

    // [API-66] GET /api/members/me/credit
    // 초안 응답: balance·creditPerQuote·availableQuoteCount
    // reservedCredit·availableCredit은 정책상 필요하지만 명세에 없는 필드 (추가 필요, 미확정)
    getMyCredit: function () {
      var balance = totalBalance();
      var reserved = reservedCredit();
      var available = balance - reserved;
      return delay({
        balance: balance,
        creditPerQuote: CREDIT_PER_QUOTE,
        availableQuoteCount: Math.floor(available / CREDIT_PER_QUOTE), // 계산 범위 Q-03 협의 중
        reservedCredit: reserved,    // 명세 미반영 (mock)
        availableCredit: available   // 명세 미반영 (mock)
      });
    },

    // [API-67] GET /api/members/me/credit-histories?type=&page=&size=
    getCreditHistories: function (params) {
      params = params || {};
      var type = params.type || '';
      var page = params.page || 1;
      var size = params.size || 10; // 기본값·최대값 Q-06 미정, mock은 10
      var all = state.histories
        .filter(function (h) { return !type || h.type === type; })
        .sort(function (a, b) { return b.createdAt.localeCompare(a.createdAt) || b.id - a.id; });
      var totalPages = Math.max(1, Math.ceil(all.length / size));
      return delay({
        content: all.slice((page - 1) * size, page * size),
        pageNumber: page, pageSize: size, totalElements: all.length,
        totalPages: totalPages, lastPage: page >= totalPages
      });
    },

    // 예약 목록 - 대응 API 없음 (미확정, 화면 표시용 mock)
    getReservations: function () {
      return delay(state.reservations);
    },

    // [API-68] POST /api/payments - READY 주문 생성. 금액은 서버 상품 가격 기준
    createPayment: function (body) {
      var product = state.products.find(function (p) { return p.id === body.creditProductId; });
      if (!product) return fail(404, '충전 상품을 찾을 수 없습니다. (mock 메시지)');
      state.seq.payment += 1;
      var payment = {
        paymentId: state.seq.payment,
        orderId: 'efy-mock-' + String(state.seq.payment).padStart(4, '0') + '-' + Date.now().toString(36),
        productId: product.id, orderName: product.name, amount: product.price,
        creditAmount: product.creditAmount + product.bonusCredit, status: 'READY', paymentKey: null
      };
      state.payments.push(payment);
      return delay({ paymentId: payment.paymentId, orderId: payment.orderId, orderName: payment.orderName, amount: payment.amount });
    },

    // [API-69] POST /api/payments/confirm - 승인·지급 (같은 주문 재요청 시 재지급 없음)
    confirmPayment: function (body) {
      var payment = findPayment(body.orderId);
      if (!payment) return fail(404, '주문을 찾을 수 없습니다. (mock 메시지)');
      if (payment.amount !== body.amount) return fail(400, '결제 금액이 주문 금액과 일치하지 않습니다. (mock 메시지)');
      if (payment.status === 'DONE') return delay(confirmResult(payment)); // 멱등 처리
      if (payment.status !== 'READY' && payment.status !== 'PENDING') {
        return fail(409, '승인할 수 없는 주문 상태입니다. (mock 메시지)');
      }
      payment.paymentKey = body.paymentKey;
      grantCredit(payment);
      return delay(confirmResult(payment));
    },

    // [API-69] 승인 응답 지연 재현 - 외부 결과를 단정하지 않고 PENDING으로 둔다
    // 지연 시 응답 형식은 명세 미정 (mock 형식)
    confirmPaymentTimeout: function (body) {
      var payment = findPayment(body.orderId);
      if (!payment) return fail(404, '주문을 찾을 수 없습니다. (mock 메시지)');
      if (payment.status === 'READY') { payment.status = 'PENDING'; payment.paymentKey = body.paymentKey; }
      return delay({ orderId: payment.orderId, paymentStatus: payment.status });
    },

    // 결제 결과 재확인 - 대응 API 없음 (CR-03 '결제 조회로 재확인', 경로 미정)
    // resolveAs: 모의로 재현할 외부 결과 ('DONE' | 'FAILED' | 'PENDING')
    recheckPayment: function (orderId, resolveAs) {
      var payment = findPayment(orderId);
      if (!payment) return fail(404, '주문을 찾을 수 없습니다. (mock 메시지)');
      if (payment.status === 'PENDING') {
        if (resolveAs === 'DONE') grantCredit(payment);
        else if (resolveAs === 'FAILED') payment.status = 'FAILED';
      }
      return delay({ orderId: payment.orderId, paymentStatus: payment.status, chargedCredit: payment.status === 'DONE' ? payment.creditAmount : 0, balance: totalBalance() });
    },

    // [API-70] PATCH /api/payments/{orderId}/fail
    // PAY_PROCESS_CANCELED → CANCELED, 그 외 → FAILED. DONE 주문은 덮어쓰지 않는다 (CR-04)
    failPayment: function (orderId, body) {
      var payment = findPayment(orderId);
      if (!payment) return fail(404, '주문을 찾을 수 없습니다. (mock 메시지)');
      if (payment.status === 'READY' || payment.status === 'PENDING') {
        payment.status = body.code === 'PAY_PROCESS_CANCELED' ? 'CANCELED' : 'FAILED';
      }
      return delay({ orderId: payment.orderId, paymentStatus: payment.status });
    },

    // 개발용: 현재 주문 목록 (상태 패널 표시)
    debugPayments: function () { return clone(state.payments); },
    debugSnapshot: function () {
      return { balance: totalBalance(), reserved: reservedCredit(), histories: state.histories.length, payments: state.payments.length };
    },

    // 개발용: mock 상태 초기화
    reset: function () { state = createInitialState(); }
  };

  global.MockApi = api;
})(window);
