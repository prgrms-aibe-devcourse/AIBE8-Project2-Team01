package com.grepp.efy.domain.credit.payment;

import com.grepp.efy.domain.credit.CreditAccessValidator;
import com.grepp.efy.domain.credit.payment.dto.*;
import com.grepp.efy.domain.credit.product.CreditProduct;
import com.grepp.efy.domain.credit.product.CreditProductService;
import com.grepp.efy.domain.credit.wallet.CreditWalletService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final String USER_CANCEL_CODE = "PAY_PROCESS_CANCELED";
    private final PaymentRepository paymentRepository;
    private final CreditProductService creditProductService;
    private final CreditAccessValidator creditAccessValidator;
    private final CreditWalletService creditWalletService;
    private final TossPaymentsClient tossPaymentsClient;

    // 선택한 상품으로 READY 주문을 만든다. 금액과 지급 크레딧은 요청이 아니라 서버의 상품 정보로 정한다
    @Transactional
    public PaymentCreateResponse createPayment(Long memberId, PaymentCreateRequest request) {
        Member member = creditAccessValidator.getExpert(memberId);
        CreditProduct product = creditProductService.getActiveProductEntity(request.getCreditProductId());

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setCreditProduct(product);
        payment.setOrderId("efy-" + UUID.randomUUID()); // 토스 규칙: 6~64자, 영문·숫자·-·_
        payment.setOrderName(product.getName());
        payment.setAmount(product.getPrice());
        payment.setCreditAmount(product.getCreditAmount());
        payment.setPaymentStatus(PaymentStatus.READY);
        paymentRepository.save(payment);

        return new PaymentCreateResponse(payment.getId(), payment.getOrderId(), payment.getOrderName(), payment.getAmount());
    }

    // 이 메서드의 BusinessException은 모두 데이터를 바꾸기 전에 던지고,
    // 토스 거절만 FAILED를 저장한 뒤 던진다. 그래서 BusinessException은 롤백하지 않는다
    @Transactional(noRollbackFor = BusinessException.class)
    public PaymentConfirmResponse confirmPayment(Long memberId, PaymentConfirmRequest request) {
        creditAccessValidator.getExpert(memberId);
        Payment payment = paymentRepository.findByOrderIdForUpdate(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND_EXCEPTION));
        if (!payment.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        // 새로고침 등으로 다시 요청해도 다시 지급하지 않고 기존 결과를 반환한다
        if (payment.getPaymentStatus() == PaymentStatus.DONE) {
            return new PaymentConfirmResponse(payment.getOrderId(), PaymentStatus.DONE,
                    payment.getCreditAmount(), creditWalletService.getBalance(memberId));
        }
        if (payment.getPaymentStatus() != PaymentStatus.READY && payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS_EXCEPTION);
        }
        if (!payment.getAmount().equals(request.getAmount())) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH_EXCEPTION);
        }

        try {
            tossPaymentsClient.confirm(request.getPaymentKey(), payment.getOrderId(), payment.getAmount());
        } catch (RestClientResponseException e) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            throw new BusinessException(ErrorCode.PAYMENT_CONFIRM_FAILED_EXCEPTION);
        } catch (ResourceAccessException e) {
            // 응답이 없으면 결제 성공 여부를 알 수 없어 지급하지 않고 PENDING으로 둔다
            payment.setPaymentStatus(PaymentStatus.PENDING);
            return new PaymentConfirmResponse(payment.getOrderId(), PaymentStatus.PENDING,
                    0, creditWalletService.getBalance(memberId));
        }

        payment.setPaymentKey(request.getPaymentKey());
        payment.setPaymentStatus(PaymentStatus.DONE);
        payment.setApprovedAt(LocalDateTime.now());
        long balance = creditWalletService.chargeForPayment(payment.getMember(), payment.getId(), payment.getCreditAmount());
        return new PaymentConfirmResponse(payment.getOrderId(), PaymentStatus.DONE, payment.getCreditAmount(), balance);
    }

    // 토스 결제창에서 실패·취소로 돌아왔을 때 READY 주문을 CANCELED 또는 FAILED로 기록한다
    @Transactional
    public PaymentFailResponse failPayment(Long memberId, String orderId, PaymentFailRequest request) {
        creditAccessValidator.getExpert(memberId);
        Payment payment = paymentRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND_EXCEPTION));
        if (!payment.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        // 이미 실패·취소로 기록된 주문은 다시 요청해도 기존 결과를 반환한다
        if (payment.getPaymentStatus() == PaymentStatus.FAILED || payment.getPaymentStatus() == PaymentStatus.CANCELED) {
            return new PaymentFailResponse(payment.getOrderId(), payment.getPaymentStatus());
        }
        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS_EXCEPTION);
        }

        PaymentStatus status = USER_CANCEL_CODE.equals(request.getCode())
                ? PaymentStatus.CANCELED
                : PaymentStatus.FAILED;
        payment.setPaymentStatus(status);
        return new PaymentFailResponse(payment.getOrderId(), status);
    }
}