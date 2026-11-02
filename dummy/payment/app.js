// EFY 크레딧 충전 더미 - 화면·라우팅
// 해시 라우팅: #/credit → #/checkout → (모의 결제창) → #/result → #/history
(function () {
  'use strict';

  var api = window.MockApi;
  var app = document.getElementById('app');

  // 화면 상태 (메모리에만 보관, 새로고침하면 초기화)
  var ui = {
    selectedProductId: null,
    currentOrder: null,     // 결제창이 열린 주문 (READY)
    results: {},            // orderId → 결제 결과
    newPaymentIds: [],      // 이번 세션에 충전 성공한 paymentId (내역 강조)
    historyType: '',
    historyPage: 1,
    busy: false,            // 처리 중 중복 클릭 방지
    lastResponse: null      // 현재 화면이 받은 mock 응답 (데이터 패널 표시)
  };

  // ---------- 공통 유틸 ----------
  function esc(v) {
    return String(v == null ? '' : v).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }
  function won(n) { return Number(n).toLocaleString('ko-KR') + '원'; }
  function fmtDate(iso) {
    // 2026-11-01T14:20:00 → 11/01 14:20
    var m = /^\d{4}-(\d{2})-(\d{2})T(\d{2}):(\d{2})/.exec(iso || '');
    return m ? m[1] + '/' + m[2] + ' ' + m[3] + ':' + m[4] : iso;
  }
  function signed(n) { return (n > 0 ? '+' : '') + n; }
  function linkBtn(href, label, primary) {
    return '<a class="btn link ' + (primary ? 'primary' : 'ghost') + '" href="' + href + '">' + label + '</a>';
  }

  var TYPE_LABEL = { CHARGE: '충전', USE: '사용', REFUND: '환불' };
  var STATUS_LABEL = { READY: '결제 대기', DONE: '승인 완료', FAILED: '실패', CANCELED: '사용자 취소', PENDING: '처리 확인 중' };

  function go(hash) { location.hash = hash; }

  function parseHash() {
    var raw = location.hash.replace(/^#/, '') || '/credit';
    var parts = raw.split('?');
    var params = {};
    (parts[1] || '').split('&').forEach(function (kv) {
      if (!kv) return;
      var p = kv.split('=');
      params[decodeURIComponent(p[0])] = decodeURIComponent(p[1] || '');
    });
    return { path: parts[0], params: params };
  }

  var toastTimer = null;
  function toast(msg) {
    var t = document.getElementById('toast');
    t.textContent = msg;
    t.hidden = false;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () { t.hidden = true; }, 2600);
  }

  // 처리 중에는 화면·결제창의 버튼을 잠근다 (연타로 인한 중복 요청 방지)
  function setBusy(flag) {
    ui.busy = flag;
    document.querySelectorAll('#app button, #pay-modal button').forEach(function (b) {
      if (flag) { b.dataset.prevDisabled = b.disabled ? '1' : ''; b.disabled = true; }
      else { b.disabled = b.dataset.prevDisabled === '1'; }
    });
  }

  // 화면별 '필요 데이터·API 연결 지점' 패널
  // status: 'draft' = 노션 명세 초안·미구현, 'none' = 대응 API 없음(미확정)
  function apiPanel(items, response) {
    var rows = items.map(function (it) {
      var cls = it.status === 'none' ? 'status-none' : 'status-draft';
      var label = it.status === 'none' ? '대응 API 없음 · 미확정' : '명세 초안 · 미구현';
      return '<li><code>' + esc(it.method) + ' ' + esc(it.path) + '</code> ' + esc(it.id || '') +
        ' <span class="' + cls + '">[' + label + ']</span><br><span class="muted">' + esc(it.note || '') + '</span></li>';
    }).join('');
    return '<details class="api"><summary>이 화면의 데이터 · API 연결 지점</summary><ul>' + rows + '</ul>' +
      (response ? '<div class="muted">마지막 mock 응답</div><pre>' + esc(JSON.stringify(response, null, 2)) + '</pre>' : '') +
      '</details>';
  }

  // 총잔액·예약·사용 가능 카드
  function balanceCards(c) {
    return '<div class="balance-grid">' +
      '<div class="card"><span class="label">총잔액</span><span class="value">' + c.balance + '</span><span class="sub">내역 합계 · balance</span></div>' +
      '<div class="card"><span class="label">견적에 예약됨</span><span class="value">' + c.reservedCredit + '</span><span class="sub">매칭 전 발송 견적 · 명세 미반영</span></div>' +
      '<div class="card strong"><span class="label">사용 가능</span><span class="value">' + c.availableCredit + '</span><span class="sub">총잔액 − 예약 · 견적 ' + c.availableQuoteCount + '건 발송 가능</span></div>' +
      '</div>';
  }

  function refreshGnb() {
    api.getMyCredit().then(function (c) {
      document.getElementById('gnb-credit').textContent = '사용 가능 ' + c.availableCredit + ' / 총 ' + c.balance;
    });
  }

  // ---------- 화면 1: 잔액 확인 + 충전 상품 선택 ----------
  function renderCredit() {
    app.innerHTML = '<p class="empty">불러오는 중…</p>';
    Promise.all([api.getMyCredit(), api.getCreditProducts()]).then(function (res) {
      var credit = res[0], products = res[1];
      ui.lastResponse = { credit: credit, products: products };
      if (!products.some(function (p) { return p.id === ui.selectedProductId; })) ui.selectedProductId = null;

      var productHtml = products.length ? products.map(function (p) {
        return '<button type="button" class="product" role="radio" aria-checked="' + (p.id === ui.selectedProductId) + '" data-product="' + p.id + '">' +
          '<span class="name">' + esc(p.name) + '</span>' +
          '<span class="price">' + won(p.price) + '</span>' +
          '<span class="muted small">충전 ' + p.creditAmount + '크레딧 · 견적 ' + Math.floor(p.creditAmount / api.CREDIT_PER_QUOTE) + '건분</span>' +
          (p.isExample ? '<span class="tag example">예시 데이터</span>' : '<span class="tag">API 명세 예시 값</span>') +
          '</button>';
      }).join('') : '<p class="empty">판매 중인 충전 상품이 없습니다.</p>';

      app.innerHTML =
        '<div><div class="step">1 / 4 · 크레딧 확인·상품 선택</div><h1>크레딧 충전</h1></div>' +
        balanceCards(credit) +
        (credit.availableCredit < api.CREDIT_PER_QUOTE ? '<p class="notice">사용 가능 크레딧이 ' + api.CREDIT_PER_QUOTE + '보다 적어 견적을 보낼 수 없습니다.</p>' : '') +
        '<p class="notice">견적 1건을 보내면 ' + api.CREDIT_PER_QUOTE + '크레딧이 예약되고, 고객이 의뢰를 요청해 매칭되면 실제로 차감됩니다. 1크레딧 = ' + won(api.CREDIT_UNIT_PRICE) + '</p>' +
        '<section><h2>충전 상품</h2><div class="products" role="radiogroup" aria-label="충전 상품">' + productHtml + '</div>' +
        '<p class="muted small">상품 구간은 문서·코드에 확정 값이 없어 예시로 구성했습니다. 가격 = 크레딧 × 1,000원 규칙만 확정입니다.</p></section>' +
        '<div class="actions"><button type="button" class="btn primary" id="btn-next" ' + (ui.selectedProductId ? '' : 'disabled') + '>다음: 결제 정보 확인</button>' +
        linkBtn('#/history', '충전·사용 내역') + '</div>' +
        apiPanel([
          { method: 'GET', path: '/api/members/me/credit', id: 'API-66', status: 'draft', note: 'balance·creditPerQuote·availableQuoteCount. 예약·사용 가능 크레딧 필드는 명세에 없음' },
          { method: 'GET', path: '/api/credit-products', id: 'API-65', status: 'draft', note: 'id·name·price·creditAmount·bonusCredit(MVP는 0). 활성 상품 가격 오름차순' }
        ], ui.lastResponse);

      app.querySelectorAll('[data-product]').forEach(function (el) {
        el.addEventListener('click', function () {
          ui.selectedProductId = Number(el.dataset.product);
          app.querySelectorAll('[data-product]').forEach(function (o) { o.setAttribute('aria-checked', String(o === el)); });
          document.getElementById('btn-next').disabled = false;
        });
      });
      document.getElementById('btn-next').addEventListener('click', function () {
        if (ui.selectedProductId) go('#/checkout?productId=' + ui.selectedProductId);
      });
    });
  }

  // ---------- 화면 2: 결제 정보 확인 + 모의 결제 ----------
  function renderCheckout(params) {
    var productId = Number(params.productId);
    app.innerHTML = '<p class="empty">불러오는 중…</p>';
    Promise.all([api.getMyCredit(), api.getCreditProducts()]).then(function (res) {
      var credit = res[0];
      var product = res[1].find(function (p) { return p.id === productId; });
      if (!product) {
        app.innerHTML = '<p class="empty">선택한 상품이 없습니다. <a href="#/credit">상품 다시 선택</a></p>';
        return;
      }
      ui.selectedProductId = product.id;
      var charge = product.creditAmount + product.bonusCredit;
      ui.lastResponse = { product: product, credit: credit };

      app.innerHTML =
        '<div><div class="step">2 / 4 · 결제 정보 확인</div><h1>결제 정보 확인</h1></div>' +
        '<div class="box"><dl class="kv">' +
          '<dt>선택 상품</dt><dd>' + esc(product.name) + (product.isExample ? ' <span class="tag example">예시 데이터</span>' : '') + '</dd>' +
          '<dt>충전 크레딧</dt><dd>' + charge + '크레딧</dd>' +
          '<dt>결제 금액</dt><dd class="total">' + won(product.price) + '</dd>' +
          '<dt>충전 후 총잔액</dt><dd>' + credit.balance + ' → ' + (credit.balance + charge) + ' (예상)</dd>' +
          '<dt>충전 후 사용 가능</dt><dd>' + credit.availableCredit + ' → ' + (credit.availableCredit + charge) + ' (예상)</dd>' +
        '</dl></div>' +
        '<p class="notice">테스트 결제입니다. 결제 금액은 서버의 상품 가격으로 정해지며 화면에서 금액을 보내지 않습니다.</p>' +
        '<div class="actions">' +
          '<button type="button" class="btn primary" id="btn-pay">' + won(product.price) + ' 결제하기</button>' +
          linkBtn('#/credit', '상품 다시 선택') +
        '</div>' +
        apiPanel([
          { method: 'POST', path: '/api/payments', id: 'API-68', status: 'draft', note: '요청 { creditProductId } → 응답 paymentId·orderId·orderName·amount. READY 주문 생성' },
          { method: '—', path: '토스 테스트 결제창 호출', id: '', status: 'draft', note: 'orderId·orderName·amount를 그대로 전달. 이 더미에서는 모의 결제창으로 대체' }
        ], ui.lastResponse);

      document.getElementById('btn-pay').addEventListener('click', function () {
        if (ui.busy) return;
        setBusy(true);
        api.createPayment({ creditProductId: product.id }).then(function (order) {
          ui.currentOrder = { orderId: order.orderId, orderName: order.orderName, amount: order.amount, paymentId: order.paymentId, productId: product.id, creditAmount: charge };
          renderDevPanel();
          setBusy(false);
          openPayModal();
        }).catch(function (e) { toast(e.message); setBusy(false); });
      });
    });
  }

  // ---------- 모의 결제창 ----------
  var modal = document.getElementById('pay-modal');

  function openPayModal() {
    var o = ui.currentOrder;
    document.getElementById('pay-modal-info').innerHTML =
      '<dt>주문명</dt><dd>' + esc(o.orderName) + '</dd>' +
      '<dt>결제 금액</dt><dd>' + won(o.amount) + '</dd>' +
      '<dt>주문번호</dt><dd class="small">' + esc(o.orderId) + '</dd>';
    modal.querySelectorAll('button').forEach(function (b) { b.disabled = false; });
    modal.hidden = false;
    modal.querySelector('[data-outcome="success"]').focus();
  }

  function closePayModal() { modal.hidden = true; }

  modal.querySelectorAll('[data-outcome]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var o = ui.currentOrder;
      if (!o || ui.busy) return; // 결제창 버튼 연타 방지
      setBusy(true);
      var outcome = btn.dataset.outcome;
      var mockKey = 'mock_pk_' + Date.now().toString(36);
      var call;
      if (outcome === 'success') {
        // successUrl로 받은 값으로 승인 요청 (paymentKey는 mock 값)
        call = api.confirmPayment({ paymentKey: mockKey, orderId: o.orderId, amount: o.amount });
      } else if (outcome === 'timeout') {
        call = api.confirmPaymentTimeout({ paymentKey: mockKey, orderId: o.orderId, amount: o.amount });
      } else if (outcome === 'cancel') {
        call = api.failPayment(o.orderId, { code: 'PAY_PROCESS_CANCELED', message: '사용자에 의해 결제가 취소되었습니다.' });
      } else {
        // 실패 코드는 mock 값 (실제 토스 에러 코드 목록은 확인하지 않음)
        call = api.failPayment(o.orderId, { code: 'MOCK_PAYMENT_FAILED', message: '모의 결제 실패' });
      }
      call.then(function (r) {
        ui.results[o.orderId] = Object.assign({
          orderName: o.orderName, amount: o.amount, creditAmount: o.creditAmount,
          paymentId: o.paymentId, productId: o.productId, chargedCredit: 0
        }, r);
        if (r.paymentStatus === 'DONE' && ui.newPaymentIds.indexOf(o.paymentId) < 0) ui.newPaymentIds.push(o.paymentId);
        ui.currentOrder = null; // 같은 주문으로 다시 결제창을 열지 않는다
        closePayModal();
        setBusy(false);
        go('#/result?orderId=' + encodeURIComponent(o.orderId));
      }).catch(function (e) {
        toast(e.message);
        setBusy(false);
      });
    });
  });

  // ---------- 화면 3: 결제 결과 ----------
  function renderResult(params) {
    var r = ui.results[params.orderId];
    if (!r) {
      app.innerHTML = '<p class="empty">결제 결과를 찾을 수 없습니다. (새로고침하면 mock 상태가 초기화됩니다)<br><a href="#/credit">크레딧 화면으로</a></p>';
      return;
    }
    ui.lastResponse = r;
    var info = '<div class="box"><dl class="kv">' +
      '<dt>주문명</dt><dd>' + esc(r.orderName) + '</dd>' +
      '<dt>결제 금액</dt><dd>' + won(r.amount) + '</dd>' +
      '<dt>주문번호</dt><dd class="small">' + esc(r.orderId) + '</dd>' +
      '<dt>결제 상태</dt><dd>' + esc(STATUS_LABEL[r.paymentStatus] || r.paymentStatus) + ' (' + esc(r.paymentStatus) + ')</dd>' +
      '</dl></div>';

    var head, actions, apis;

    if (r.paymentStatus === 'DONE') {
      head = '<div class="result ok"><strong>충전이 완료되었습니다</strong><span>' + r.chargedCredit + '크레딧이 충전되어 총잔액이 ' + r.balance + '크레딧이 되었습니다.</span></div>';
      actions = linkBtn('#/history', '잔액·내역 확인', true) + linkBtn('#/credit', '크레딧 화면으로') +
        '<button type="button" class="btn ghost" id="btn-reconfirm">같은 주문 승인 재요청 (중복 지급 확인용)</button>';
      apis = [{ method: 'POST', path: '/api/payments/confirm', id: 'API-69', status: 'draft', note: '요청 paymentKey·orderId·amount → 응답 orderId·paymentStatus·chargedCredit·balance. 이미 DONE이면 재지급 없이 같은 결과' }];
    } else if (r.paymentStatus === 'FAILED') {
      head = '<div class="result err"><strong>결제에 실패했습니다</strong><span>크레딧은 지급되지 않았습니다. 다시 시도하거나 다른 상품을 선택해 주세요.</span></div>';
      actions = '<button type="button" class="btn primary" id="btn-retry">다시 결제하기</button>' + linkBtn('#/credit', '상품 다시 선택');
      apis = [{ method: 'PATCH', path: '/api/payments/{orderId}/fail', id: 'API-70', status: 'draft', note: '요청 code·message(failUrl 값) → paymentStatus FAILED. 실패 코드 값은 mock' }];
    } else if (r.paymentStatus === 'CANCELED') {
      head = '<div class="result cancel"><strong>결제를 취소했습니다</strong><span>크레딧은 지급되지 않았습니다.</span></div>';
      actions = '<button type="button" class="btn primary" id="btn-retry">다시 결제하기</button>' + linkBtn('#/credit', '상품 다시 선택');
      apis = [{ method: 'PATCH', path: '/api/payments/{orderId}/fail', id: 'API-70', status: 'draft', note: 'code PAY_PROCESS_CANCELED → paymentStatus CANCELED' }];
    } else {
      // PENDING: 결과를 단정하지 않고 재확인
      head = '<div class="result pending"><strong>결제 결과를 확인하고 있습니다</strong><span>승인 결과가 확인되기 전에는 크레딧이 지급되지 않습니다. 잠시 후 결과를 다시 확인해 주세요.</span></div>';
      actions = '<label class="small muted recheck">재확인 시 재현할 결과(mock)' +
          '<select id="recheck-as"><option value="DONE">승인 완료</option><option value="FAILED">실패</option><option value="PENDING">아직 확인 중</option></select></label>' +
        '<button type="button" class="btn primary" id="btn-recheck">결과 다시 확인</button>' + linkBtn('#/history', '내역 보기');
      apis = [
        { method: 'POST', path: '/api/payments/confirm', id: 'API-69', status: 'draft', note: '토스 응답 지연 시 응답 형식은 명세 미정' },
        { method: '?', path: '결제 결과 조회(재확인)', id: '', status: 'none', note: 'CR-03 "결제 조회로 재확인"의 경로·응답 미정' }
      ];
    }

    app.innerHTML = '<div><div class="step">3 / 4 · 결제 결과</div><h1>결제 결과</h1></div>' + head + info +
      '<div class="actions">' + actions + '</div>' + apiPanel(apis, ui.lastResponse);

    var reconfirm = document.getElementById('btn-reconfirm');
    if (reconfirm) reconfirm.addEventListener('click', function () {
      if (ui.busy) return;
      setBusy(true);
      api.confirmPayment({ paymentKey: 'mock_pk_retry', orderId: r.orderId, amount: r.amount }).then(function (res) {
        toast('이미 승인된 주문입니다 · 재지급 없음 (총잔액 ' + res.balance + ')');
        renderDevPanel();
      }).catch(function (e) { toast(e.message); }).then(function () { setBusy(false); });
    });

    var retry = document.getElementById('btn-retry');
    if (retry) retry.addEventListener('click', function () {
      // 새 주문으로 다시 결제 (FAILED·CANCELED 주문은 재사용하지 않음)
      go('#/checkout?productId=' + (r.productId || ui.selectedProductId || ''));
    });

    var recheck = document.getElementById('btn-recheck');
    if (recheck) recheck.addEventListener('click', function () {
      if (ui.busy) return;
      setBusy(true);
      var as = document.getElementById('recheck-as').value;
      api.recheckPayment(r.orderId, as).then(function (res) {
        Object.assign(r, res);
        if (res.paymentStatus === 'DONE' && ui.newPaymentIds.indexOf(r.paymentId) < 0) ui.newPaymentIds.push(r.paymentId);
        if (res.paymentStatus === 'PENDING') toast('아직 결과를 확인하고 있습니다.');
        setBusy(false);
        route();
      }).catch(function (e) { toast(e.message); setBusy(false); });
    });
  }

  // ---------- 화면 4: 충전·사용 내역 ----------
  function renderHistory() {
    app.innerHTML = '<p class="empty">불러오는 중…</p>';
    Promise.all([
      api.getMyCredit(),
      api.getCreditHistories({ type: ui.historyType, page: ui.historyPage }),
      api.getReservations()
    ]).then(function (res) {
      var credit = res[0], page = res[1], reservations = res[2];
      ui.lastResponse = { credit: credit, histories: page, reservations: reservations };

      var filters = [['', '전체'], ['CHARGE', '충전'], ['USE', '사용'], ['REFUND', '환불']].map(function (f) {
        return '<button type="button" data-type="' + f[0] + '" aria-pressed="' + (ui.historyType === f[0]) + '">' + f[1] + '</button>';
      }).join('');

      var rows = page.content.map(function (h) {
        var rel = h.paymentId ? '결제 #' + h.paymentId : (h.quoteId ? '견적 #' + h.quoteId : '-');
        var isNew = h.paymentId && ui.newPaymentIds.indexOf(h.paymentId) >= 0;
        return '<tr' + (isNew ? ' class="new"' : '') + '><td class="muted">' + fmtDate(h.createdAt) + '</td><td>' + (TYPE_LABEL[h.type] || h.type) + (isNew ? ' <span class="tag">방금 충전</span>' : '') + '</td>' +
          '<td>' + rel + '</td><td class="num">' + signed(h.amount) + '</td><td class="num">' + h.balanceAfter + '</td></tr>';
      }).join('');

      var resvRows = reservations.map(function (r) {
        return '<tr><td class="muted">' + fmtDate(r.sentAt) + '</td><td>견적 #' + r.quoteId + '</td><td class="num">' + r.credit + '</td></tr>';
      }).join('');

      app.innerHTML =
        '<div><div class="step">4 / 4 · 잔액·내역 확인</div><h1>충전·사용 내역</h1></div>' +
        balanceCards(credit) +
        '<section><h2>내역</h2><div class="filters" role="group" aria-label="유형 필터">' + filters + '</div></section>' +
        '<div class="table-wrap"><table><thead><tr><th>일시</th><th>유형</th><th>관련</th><th class="num">변동</th><th class="num">변동 후 잔액</th></tr></thead><tbody>' +
          (rows || '<tr><td colspan="5" class="empty">내역이 없습니다.</td></tr>') + '</tbody></table></div>' +
        '<div class="pager"><button type="button" class="btn small ghost" id="pg-prev" ' + (page.pageNumber <= 1 ? 'disabled' : '') + '>이전</button>' +
          '<span>' + page.pageNumber + ' / ' + page.totalPages + ' 페이지 · 총 ' + page.totalElements + '건</span>' +
          '<button type="button" class="btn small ghost" id="pg-next" ' + (page.lastPage ? 'disabled' : '') + '>다음</button></div>' +
        '<section><h2>견적에 예약된 크레딧 <span class="tag example">mock · 조회 API 미정</span></h2>' +
          '<p class="muted small">예약은 차감이 아니므로 위 내역(충전·사용·환불)에 포함되지 않습니다. 매칭되면 사용 내역으로 기록됩니다.</p>' +
          '<div class="table-wrap"><table><thead><tr><th>발송 일시</th><th>견적</th><th class="num">예약 크레딧</th></tr></thead><tbody>' +
          (resvRows || '<tr><td colspan="3" class="empty">예약된 크레딧이 없습니다.</td></tr>') + '</tbody></table></div></section>' +
        '<div class="actions">' + linkBtn('#/credit', '크레딧 충전하기', true) + '</div>' +
        apiPanel([
          { method: 'GET', path: '/api/members/me/credit', id: 'API-66', status: 'draft', note: '잔액 카드. 예약·사용 가능 필드는 명세 추가 필요' },
          { method: 'GET', path: '/api/members/me/credit-histories?type=&page=&size=', id: 'API-67', status: 'draft', note: 'PageResponse. id·type·amount·balanceAfter·paymentId·quoteId·createdAt. size 기본값 미정' },
          { method: '?', path: '예약 중인 견적 목록', id: '', status: 'none', note: '예약 내역 표시 여부·API 미정' }
        ], ui.lastResponse);

      app.querySelectorAll('[data-type]').forEach(function (b) {
        b.addEventListener('click', function () { ui.historyType = b.dataset.type; ui.historyPage = 1; renderHistory(); });
      });
      document.getElementById('pg-prev').addEventListener('click', function () { ui.historyPage -= 1; renderHistory(); });
      document.getElementById('pg-next').addEventListener('click', function () { ui.historyPage += 1; renderHistory(); });
    });
  }

  // ---------- 개발 패널 ----------
  function renderDevPanel() {
    var s = api.debugSnapshot();
    var rows = api.debugPayments().slice().reverse().map(function (p) {
      return '<tr><td>' + p.paymentId + '</td><td>' + esc(p.orderId) + '</td><td>' + esc(p.orderName) + '</td><td class="num">' + won(p.amount) + '</td><td>' + p.status + '</td></tr>';
    }).join('');
    document.getElementById('devpanel-body').innerHTML =
      '<p>mock 총잔액 <b>' + s.balance + '</b> · 예약 <b>' + s.reserved + '</b> · 사용 가능 <b>' + (s.balance - s.reserved) + '</b> · 내역 ' + s.histories + '건 · 주문 ' + s.payments + '건 ' +
      '<span class="muted">(DONE이 된 주문만 잔액·내역에 1회 반영)</span></p>' +
      '<table><thead><tr><th>paymentId</th><th>orderId</th><th>상품</th><th class="num">금액</th><th>상태</th></tr></thead><tbody>' + rows + '</tbody></table>';
    refreshGnb();
  }

  document.getElementById('btn-reset').addEventListener('click', function () {
    if (ui.busy) return;
    api.reset();
    ui.selectedProductId = null; ui.currentOrder = null; ui.results = {}; ui.newPaymentIds = [];
    ui.historyType = ''; ui.historyPage = 1;
    closePayModal();
    toast('mock 데이터를 초기 상태로 되돌렸습니다.');
    if (location.hash === '#/credit') route(); else go('#/credit');
  });

  document.getElementById('btn-toggle-dev').addEventListener('click', function () {
    var panel = document.getElementById('devpanel');
    var collapsed = panel.classList.toggle('collapsed');
    document.body.classList.toggle('dev-collapsed', collapsed);
    this.textContent = collapsed ? '펼치기' : '접기';
    this.setAttribute('aria-expanded', String(!collapsed));
  });

  // ---------- 라우터 ----------
  function route() {
    var h = parseHash();
    // 결제창이 열린 채 다른 화면으로 이동하면 창만 닫는다 (주문은 READY로 남음)
    if (!modal.hidden) { closePayModal(); ui.currentOrder = null; }
    document.querySelectorAll('[data-nav]').forEach(function (a) {
      var nav = a.dataset.nav;
      a.classList.toggle('active', '/' + nav === h.path || (nav === 'credit' && (h.path === '/checkout' || h.path === '/result')));
    });
    if (h.path === '/checkout') renderCheckout(h.params);
    else if (h.path === '/result') renderResult(h.params);
    else if (h.path === '/history') renderHistory();
    else renderCredit();
    renderDevPanel();
    window.scrollTo(0, 0);
  }

  window.addEventListener('hashchange', route);
  route();
})();
