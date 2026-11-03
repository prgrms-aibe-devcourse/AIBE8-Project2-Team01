package com.grepp.efy.domain.credit;

import com.grepp.efy.domain.credit.reservation.CreditReservationService;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @AfterEach
    void cleanUp() {
        for (Long memberId : memberIds) {
            String wallet = "(select id from credit_wallet where member_id = ?)";
            jdbcTemplate.update("delete from credit_history where credit_wallet_id in " + wallet, memberId);
            jdbcTemplate.update("delete from credit_reservation where credit_wallet_id in " + wallet, memberId);
            jdbcTemplate.update("delete from credit_wallet where member_id = ?", memberId);
            jdbcTemplate.update("delete from member where id = ?", memberId);
        }
    }

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

    private void assertError(ErrorCode expected, org.junit.jupiter.api.function.Executable executable) {
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

    private Long balance(Long memberId) {
        return jdbcTemplate.queryForObject("select balance from credit_wallet where member_id = ?", Long.class, memberId);
    }

    private Long nextQuoteId() {
        return QUOTE_ID.incrementAndGet();
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

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
}