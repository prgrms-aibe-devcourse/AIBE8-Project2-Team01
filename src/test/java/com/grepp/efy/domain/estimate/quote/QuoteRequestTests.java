package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 의뢰 요청 시 견적서 검증·상태 전이 (requestQuote) 테스트. 각 테스트는 롤백된다
@SpringBootTest
@Transactional
class QuoteRequestTests {

    @Autowired
    QuoteService quoteService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    EntityManager entityManager;

    Long customerId;
    Long otherCustomerId;
    Long expertProfileId;

    @BeforeEach
    void setUp() {
        customerId = memberId("customer1@example.com");
        otherCustomerId = memberId("customer2@example.com");
        expertProfileId = jdbcTemplate.queryForObject(
                "select ep.id from expert_profile ep join member m on ep.member_id = m.id where m.email = ?",
                Long.class, "expert1@example.com");
    }

    @Test
    void 유효한_SENT_견적에_의뢰요청하면_견적은_REQUESTED_요청서는_MATCHED() {
        Long requestId = createRequest(customerId, "OPEN", 1);
        Long quoteId = createQuote(requestId, "SENT", 1);

        Quote quote = quoteService.requestQuote(customerId, quoteId);
        entityManager.flush();

        assertEquals(QuoteStatus.REQUESTED, quote.getQuoteStatus());
        assertEquals("REQUESTED", quoteStatus(quoteId));
        assertEquals("MATCHED", requestStatus(requestId));
        assertEquals("USED", reservationStatus(quoteId));
        assertEquals(1, useHistoryCount(quoteId));
    }

    @Test
    void 접수기간이_지난_요청서도_유효한_견적이면_의뢰요청_가능() {
        Long requestId = createRequest(customerId, "OPEN", 25);
        Long quoteId = createQuote(requestId, "SENT", 10);

        quoteService.requestQuote(customerId, quoteId);
        entityManager.flush();

        assertEquals("REQUESTED", quoteStatus(quoteId));
        assertEquals("MATCHED", requestStatus(requestId));
    }

    @Test
    void 이미_MATCHED인_요청서는_상태를_유지하고_견적만_REQUESTED() {
        Long requestId = createRequest(customerId, "MATCHED", 1);
        Long quoteId = createQuote(requestId, "SENT", 1);

        quoteService.requestQuote(customerId, quoteId);
        entityManager.flush();

        assertEquals("REQUESTED", quoteStatus(quoteId));
        assertEquals("MATCHED", requestStatus(requestId));
    }

    @Test
    void 요청서_작성자가_아니면_거절() {
        Long requestId = createRequest(customerId, "OPEN", 1);
        Long quoteId = createQuote(requestId, "SENT", 1);

        assertError(ErrorCode.FORBIDDEN_EXCEPTION, () -> quoteService.requestQuote(otherCustomerId, quoteId));
        assertEquals("SENT", quoteStatus(quoteId));
        assertEquals("RESERVED", reservationStatus(quoteId));
    }

    @Test
    void 이미_의뢰요청된_견적은_거절() {
        Long requestId = createRequest(customerId, "MATCHED", 1);
        Long quoteId = createQuote(requestId, "REQUESTED", 1);

        assertError(ErrorCode.ALREADY_REQUESTED_QUOTE_EXCEPTION, () -> quoteService.requestQuote(customerId, quoteId));
    }

    @Test
    void 발송후_72시간이_지난_견적은_없는_견적으로_거절() {
        Long requestId = createRequest(customerId, "OPEN", 80);
        Long quoteId = createQuote(requestId, "SENT", 73);

        assertError(ErrorCode.QUOTE_NOT_FOUND_EXCEPTION, () -> quoteService.requestQuote(customerId, quoteId));
    }

    @Test
    void 직접_취소된_요청서의_견적은_거절() {
        Long requestId = createRequest(customerId, "CLOSED", 1);
        Long quoteId = createQuote(requestId, "SENT", 1);

        assertError(ErrorCode.CLOSED_REQUEST_QUOTE_REQUEST_NOT_ALLOWED_EXCEPTION,
                () -> quoteService.requestQuote(customerId, quoteId));
        assertEquals("CLOSED", requestStatus(requestId));
    }

    @Test
    void 없는_견적은_거절() {
        assertError(ErrorCode.QUOTE_NOT_FOUND_EXCEPTION, () -> quoteService.requestQuote(customerId, Long.MAX_VALUE));
    }

    // ── 테스트 데이터 ──

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    // 이사(101)·강남구(39) 요청서. expert1과 매칭된다
    private Long createRequest(Long memberId, String status, int hoursAgo) {
        jdbcTemplate.update("""
                insert into service_request (member_id, second_category_id, second_region_id, request_status, created_at)
                values (?, 101, 39, ?, dateadd('HOUR', ?, current_timestamp))
                """, memberId, status, -hoursAgo);
        return jdbcTemplate.queryForObject("select max(id) from service_request", Long.class);
    }

    private Long createQuote(Long requestId, String status, int hoursAgo) {
        jdbcTemplate.update("""
                insert into quote (expert_id, service_request_id, description, price, quote_status, sent_at)
                values (?, ?, '테스트 견적', 100000, ?, dateadd('HOUR', ?, current_timestamp))
                """, expertProfileId, requestId, status, -hoursAgo);
        Long quoteId = jdbcTemplate.queryForObject("select max(id) from quote", Long.class);

        // 견적 발송 시 2크레딧 예약 (expert1 지갑)
        jdbcTemplate.update("""
                insert into credit_reservation (credit_wallet_id, quote_id, amount, reservation_status, created_at)
                values ((select cw.id from credit_wallet cw join expert_profile ep on ep.member_id = cw.member_id
                         where ep.id = ?), ?, 2, 'RESERVED', current_timestamp)
                """, expertProfileId, quoteId);
        return quoteId;
    }

    private String quoteStatus(Long quoteId) {
        return jdbcTemplate.queryForObject("select quote_status from quote where id = ?", String.class, quoteId);
    }

    private String requestStatus(Long requestId) {
        return jdbcTemplate.queryForObject("select request_status from service_request where id = ?", String.class, requestId);
    }

    private String reservationStatus(Long quoteId) {
        return jdbcTemplate.queryForObject(
                "select reservation_status from credit_reservation where quote_id = ?", String.class, quoteId);
    }

    private int useHistoryCount(Long quoteId) {
        return jdbcTemplate.queryForObject(
                "select count(*) from credit_history where quote_id = ? and history_type = 'USE'", Integer.class, quoteId);
    }

    private void assertError(ErrorCode expected, Executable executable) {
        BusinessException e = assertThrows(BusinessException.class, executable);
        assertEquals(expected, e.getErrorCode());
    }
}