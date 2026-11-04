package com.grepp.efy.domain.credit;

import com.grepp.efy.domain.credit.payment.TossPaymentsClient;
import com.grepp.efy.domain.credit.reservation.CreditReservationService;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreditTests {
    private static final String URL = "/api/members/me/credit";
    private static final AtomicLong QUOTE_ID = new AtomicLong(System.currentTimeMillis());
    private final List<Long> memberIds = new ArrayList<>();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    MemberService memberService;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    CreditReservationService creditReservationService;
    @MockitoBean
    TossPaymentsClient tossPaymentsClient; // 실제 토스 대신 가짜 객체. 설정이 없으면 승인 성공처럼 동작

    @AfterEach
    void cleanUp() {
        for (Long memberId : memberIds) {
            jdbcTemplate.update("delete from payment where member_id = ?", memberId);
            String wallet = "(select id from credit_wallet where member_id = ?)";
            jdbcTemplate.update("delete from credit_history where credit_wallet_id in " + wallet, memberId);
            jdbcTemplate.update("delete from credit_reservation where credit_wallet_id in " + wallet, memberId);
            jdbcTemplate.update("delete from credit_wallet where member_id = ?", memberId);
            jdbcTemplate.update("delete from member where id = ?", memberId);
        }
    }

    // ===== 잔액 조회 =====

    @Test
    void 지갑이_없는_고수는_잔액0으로_조회() throws Exception {
        Long memberId = createMember("EXPERT");
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.reservedCredit").value(0))
                .andExpect(jsonPath("$.creditPerQuote").value(2))
                .andExpect(jsonPath("$.availableQuoteCount").value(0));
    }

    @Test
    void 예약된_크레딧을_빼고_발송가능건수를_계산() throws Exception {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 10);
        creditReservationService.reserveForQuote(memberId, nextQuoteId());
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10))
                .andExpect(jsonPath("$.reservedCredit").value(2))
                .andExpect(jsonPath("$.availableQuoteCount").value(4));
    }

    @Test
    void 미인증과_고수가_아닌_회원의_잔액조회_거절() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
        Long userId = createMember("USER");
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(userId)))
                .andExpect(status().isForbidden());
    }

    // ===== 예약 목록 =====

    @Test
    void 예약중인_견적만_조회() throws Exception {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 10);
        Long reservedQuote = nextQuoteId();
        Long releasedQuote = nextQuoteId();
        creditReservationService.reserveForQuote(memberId, reservedQuote);
        creditReservationService.reserveForQuote(memberId, releasedQuote);
        creditReservationService.releaseReservation(releasedQuote);

        mockMvc.perform(get("/api/members/me/credit-reservations").header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quoteId").value(reservedQuote))
                .andExpect(jsonPath("$[0].amount").value(2));

        Long noWalletId = createMember("EXPERT");
        mockMvc.perform(get("/api/members/me/credit-reservations").header("Authorization", "Bearer " + token(noWalletId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        Long userId = createMember("USER");
        mockMvc.perform(get("/api/members/me/credit-reservations").header("Authorization", "Bearer " + token(userId)))
                .andExpect(status().isForbidden());
    }


    // ===== 예약·차감·해제 =====

    @Test
    void 사용가능_크레딧이_부족하면_예약거절() {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 3);
        creditReservationService.reserveForQuote(memberId, nextQuoteId());
        assertError(ErrorCode.INSUFFICIENT_CREDIT_EXCEPTION,
                () -> creditReservationService.reserveForQuote(memberId, nextQuoteId()));

        Long noWalletId = createMember("EXPERT");
        assertError(ErrorCode.INSUFFICIENT_CREDIT_EXCEPTION,
                () -> creditReservationService.reserveForQuote(noWalletId, nextQuoteId()));
    }

    @Test
    void 같은_견적서는_한번만_예약() {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 10);
        Long quoteId = nextQuoteId();
        creditReservationService.reserveForQuote(memberId, quoteId);
        assertError(ErrorCode.DUPLICATE_CREDIT_RESERVATION_EXCEPTION,
                () -> creditReservationService.reserveForQuote(memberId, quoteId));
    }

    @Test
    void 의뢰요청은_한번만_차감하고_사용내역을_남김() {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 10);
        Long quoteId = nextQuoteId();
        creditReservationService.reserveForQuote(memberId, quoteId);

        creditReservationService.chargeForConnection(quoteId);
        assertError(ErrorCode.INVALID_CREDIT_RESERVATION_STATUS_EXCEPTION,
                () -> creditReservationService.chargeForConnection(quoteId));

        assertEquals(8L, balance(memberId));
        assertEquals("USED", jdbcTemplate.queryForObject(
                "select reservation_status from credit_reservation where quote_id = ?", String.class, quoteId));
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from credit_history where quote_id = ? and history_type = 'USE' and amount = -2 and balance_after = 8",
                Integer.class, quoteId));
    }

    @Test
    void 해제된_예약은_차감되지_않고_다시_사용할수있음() {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 2);
        Long quoteId = nextQuoteId();
        creditReservationService.reserveForQuote(memberId, quoteId);

        creditReservationService.releaseReservation(quoteId);
        creditReservationService.releaseReservation(quoteId);       // 두 번 호출해도 오류 없음
        creditReservationService.releaseReservation(nextQuoteId()); // 예약 없는 견적서도 오류 없음

        assertError(ErrorCode.INVALID_CREDIT_RESERVATION_STATUS_EXCEPTION,
                () -> creditReservationService.chargeForConnection(quoteId));
        assertEquals(2L, balance(memberId));
        creditReservationService.reserveForQuote(memberId, nextQuoteId()); // 해제된 2크레딧으로 다시 예약 가능
    }

    @Test
    void 동시에_예약해도_보유크레딧을_넘지않음() throws Exception {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 2);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Long quoteId = nextQuoteId();
            results.add(pool.submit(() -> {
                start.await();
                try {
                    creditReservationService.reserveForQuote(memberId, quoteId);
                    return true;
                } catch (BusinessException e) {
                    return false;
                }
            }));
        }
        start.countDown();
        int success = 0;
        for (Future<Boolean> result : results) {
            if (result.get(10, TimeUnit.SECONDS)) {
                success++;
            }
        }
        pool.shutdown();
        assertEquals(1, success);
    }

    // ===== 충전 상품 =====

    @Test
    void 판매중인_충전상품을_가격순으로_조회() throws Exception {
        Long memberId = createMember("EXPERT");
        mockMvc.perform(get("/api/credit-products").header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].price").value(10000))
                .andExpect(jsonPath("$[0].creditAmount").value(10))
                .andExpect(jsonPath("$[3].price").value(100000));

        Long userId = createMember("USER");
        mockMvc.perform(get("/api/credit-products").header("Authorization", "Bearer " + token(userId)))
                .andExpect(status().isForbidden());
    }

    // ===== 충전 주문 생성 =====

    @Test
    void 서버의_상품가격으로_READY_주문을_생성() throws Exception {
        Long memberId = createMember("EXPERT");
        // 요청에 금액을 넣어도 무시하고 서버의 상품 가격으로 주문한다
        mockMvc.perform(post("/api/payments").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"creditProductId\":2,\"amount\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(startsWith("efy-")))
                .andExpect(jsonPath("$.orderName").value("크레딧 30개"))
                .andExpect(jsonPath("$.amount").value(30000));
        assertEquals("READY", jdbcTemplate.queryForObject(
                "select payment_status from payment where member_id = ?", String.class, memberId));
        assertEquals(30, jdbcTemplate.queryForObject(
                "select credit_amount from payment where member_id = ?", Integer.class, memberId));
    }

    @Test
    void 잘못된_상품과_권한없는_주문생성_거절() throws Exception {
        Long expertId = createMember("EXPERT");
        for (String body : List.of("{}", "{\"creditProductId\":0}", "{")) {
            mockMvc.perform(post("/api/payments").header("Authorization", "Bearer " + token(expertId))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        mockMvc.perform(post("/api/payments").header("Authorization", "Bearer " + token(expertId))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"creditProductId\":999}"))
                .andExpect(status().isNotFound());

        Long userId = createMember("USER");
        mockMvc.perform(post("/api/payments").header("Authorization", "Bearer " + token(userId))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"creditProductId\":1}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content("{\"creditProductId\":1}"))
                .andExpect(status().isUnauthorized());
    }

    // ===== 결제 승인 =====

    @Test
    void 승인하면_크레딧을_한번만_지급() throws Exception {
        Long memberId = createMember("EXPERT");
        String orderId = createOrder(memberId, 2); // 크레딧 30개, 30,000원
        String body = "{\"paymentKey\":\"test_pk_1\",\"orderId\":\"" + orderId + "\",\"amount\":30000}";

        for (int i = 0; i < 2; i++) { // 같은 주문을 두 번 승인 요청
            mockMvc.perform(post("/api/payments/confirm").header("Authorization", "Bearer " + token(memberId))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentStatus").value("DONE"))
                    .andExpect(jsonPath("$.chargedCredit").value(30))
                    .andExpect(jsonPath("$.balance").value(30));
        }
        verify(tossPaymentsClient, times(1)).confirm(anyString(), eq(orderId), eq(30000L));
        assertEquals(30L, balance(memberId));
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from credit_history where credit_wallet_id = (select id from credit_wallet where member_id = ?)"
                        + " and history_type = 'CHARGE' and amount = 30 and balance_after = 30",
                Integer.class, memberId));
    }

    @Test
    void 금액이_다르거나_남의_주문이면_승인거절() throws Exception {
        Long memberId = createMember("EXPERT");
        String orderId = createOrder(memberId, 1);
        mockMvc.perform(post("/api/payments/confirm").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentKey\":\"test_pk_2\",\"orderId\":\"" + orderId + "\",\"amount\":100}"))
                .andExpect(status().isBadRequest());

        Long otherId = createMember("EXPERT");
        mockMvc.perform(post("/api/payments/confirm").header("Authorization", "Bearer " + token(otherId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentKey\":\"test_pk_2\",\"orderId\":\"" + orderId + "\",\"amount\":10000}"))
                .andExpect(status().isForbidden());
        verify(tossPaymentsClient, never()).confirm(anyString(), anyString(), anyLong());
    }

    @Test
    void 토스가_거절하면_FAILED로_기록하고_지급하지_않음() throws Exception {
        Long memberId = createMember("EXPERT");
        String orderId = createOrder(memberId, 1);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(tossPaymentsClient).confirm(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/api/payments/confirm").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentKey\":\"test_pk_3\",\"orderId\":\"" + orderId + "\",\"amount\":10000}"))
                .andExpect(status().isBadGateway());
        assertEquals("FAILED", jdbcTemplate.queryForObject(
                "select payment_status from payment where order_id = ?", String.class, orderId));
        assertEquals(0, jdbcTemplate.queryForObject(
                "select count(*) from credit_wallet where member_id = ?", Integer.class, memberId));
    }

    @Test
    void 사용자취소는_CANCELED_그외는_FAILED로_기록() throws Exception {
        Long memberId = createMember("EXPERT");
        String orderId = createOrder(memberId, 1);
        mockMvc.perform(patch("/api/payments/" + orderId + "/fail").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"PAY_PROCESS_CANCELED\",\"message\":\"사용자가 결제를 취소했습니다.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("CANCELED"));

        Long otherId = createMember("EXPERT");
        String otherOrderId = createOrder(otherId, 1);
        for (int i = 0; i < 2; i++) { // 같은 실패를 두 번 기록해도 결과가 같다
            mockMvc.perform(patch("/api/payments/" + otherOrderId + "/fail").header("Authorization", "Bearer " + token(otherId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"code\":\"REJECT_CARD_COMPANY\",\"message\":\"카드사 거절\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentStatus").value("FAILED"));
        }
        assertEquals("CANCELED", jdbcTemplate.queryForObject(
                "select payment_status from payment where order_id = ?", String.class, orderId));
        assertEquals("FAILED", jdbcTemplate.queryForObject(
                "select payment_status from payment where order_id = ?", String.class, otherOrderId));
    }

    @Test
    void 승인완료_주문과_남의_주문은_실패기록_거절() throws Exception {
        Long memberId = createMember("EXPERT");
        String orderId = createOrder(memberId, 1);
        mockMvc.perform(post("/api/payments/confirm").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentKey\":\"test_pk_4\",\"orderId\":\"" + orderId + "\",\"amount\":10000}"))
                .andExpect(status().isOk());

        String failBody = "{\"code\":\"PAY_PROCESS_CANCELED\",\"message\":\"취소\"}";
        mockMvc.perform(patch("/api/payments/" + orderId + "/fail").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content(failBody))
                .andExpect(status().isConflict());

        Long otherId = createMember("EXPERT");
        mockMvc.perform(patch("/api/payments/" + orderId + "/fail").header("Authorization", "Bearer " + token(otherId))
                        .contentType(MediaType.APPLICATION_JSON).content(failBody))
                .andExpect(status().isForbidden());
        assertEquals("DONE", jdbcTemplate.queryForObject(
                "select payment_status from payment where order_id = ?", String.class, orderId));
    }

    @Test
    void 내역을_최신순으로_조회하고_유형으로_필터() throws Exception {
        Long memberId = createMember("EXPERT");
        createWallet(memberId, 26);
        Long walletId = jdbcTemplate.queryForObject("select id from credit_wallet where member_id = ?", Long.class, memberId);
        LocalDateTime base = LocalDateTime.of(2026, 11, 1, 10, 0);
        String sql = "insert into credit_history(credit_wallet_id, history_type, amount, balance_after, created_at) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, walletId, "CHARGE", 30, 30, base);
        jdbcTemplate.update(sql, walletId, "USE", -2, 28, base.plusMinutes(1));
        jdbcTemplate.update(sql, walletId, "USE", -2, 26, base.plusMinutes(2));

        mockMvc.perform(get("/api/members/me/credit-histories?pageNumber=1&pageSize=2")
                        .header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].balanceAfter").value(26))
                .andExpect(jsonPath("$.content[1].balanceAfter").value(28))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.lastPage").value(false));

        mockMvc.perform(get("/api/members/me/credit-histories?type=CHARGE")
                        .header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].historyType").value("CHARGE"))
                .andExpect(jsonPath("$.content[0].amount").value(30));

        Long noWalletId = createMember("EXPERT"); // 지갑이 없으면 빈 목록
        mockMvc.perform(get("/api/members/me/credit-histories")
                        .header("Authorization", "Bearer " + token(noWalletId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void 잘못된_내역조회_조건과_권한_거절() throws Exception {
        Long memberId = createMember("EXPERT");
        for (String query : List.of("pageNumber=0", "pageSize=51", "type=UNKNOWN")) {
            mockMvc.perform(get("/api/members/me/credit-histories?" + query)
                            .header("Authorization", "Bearer " + token(memberId)))
                    .andExpect(status().isBadRequest());
        }
        Long userId = createMember("USER");
        mockMvc.perform(get("/api/members/me/credit-histories")
                        .header("Authorization", "Bearer " + token(userId)))
                .andExpect(status().isForbidden());
    }
    // ===== 도우미 메서드 =====

    private void assertError(ErrorCode expected, Executable executable) {
        BusinessException e = assertThrows(BusinessException.class, executable);
        assertEquals(expected, e.getErrorCode());
    }

    private Long createMember(String role) {
        String suffix = UUID.randomUUID().toString();
        String email = suffix + "@test.local";
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String phone = String.format("%011d", nextId);
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)",
                email, suffix.substring(0, 20), phone, role);
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }

    private void createWallet(Long memberId, long balance) {
        jdbcTemplate.update("insert into credit_wallet(member_id, balance, created_at, updated_at) values (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                memberId, balance);
    }

    private String createOrder(Long memberId, long productId) throws Exception {
        mockMvc.perform(post("/api/payments").header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"creditProductId\":" + productId + "}"))
                .andExpect(status().isCreated());
        return jdbcTemplate.queryForObject("select order_id from payment where member_id = ?", String.class, memberId);
    }

    private Long balance(Long memberId) {
        return jdbcTemplate.queryForObject("select balance from credit_wallet where member_id = ?", Long.class, memberId);
    }

    private Long nextQuoteId() {
        return QUOTE_ID.incrementAndGet();
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }
}