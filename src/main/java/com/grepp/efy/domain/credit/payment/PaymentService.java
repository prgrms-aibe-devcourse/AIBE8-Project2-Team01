package com.grepp.efy.domain.credit.payment;

import com.grepp.efy.domain.credit.CreditAccessValidator;
import com.grepp.efy.domain.credit.payment.dto.PaymentCreateRequest;
import com.grepp.efy.domain.credit.payment.dto.PaymentCreateResponse;
import com.grepp.efy.domain.credit.product.CreditProduct;
import com.grepp.efy.domain.credit.product.CreditProductService;
import com.grepp.efy.domain.user.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CreditProductService creditProductService;
    private final CreditAccessValidator creditAccessValidator;

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
}