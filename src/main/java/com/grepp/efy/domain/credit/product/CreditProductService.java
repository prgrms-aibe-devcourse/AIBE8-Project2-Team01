package com.grepp.efy.domain.credit.product;

import com.grepp.efy.domain.credit.product.dto.CreditProductResponse;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditProductService {

    private final CreditProductRepository creditProductRepository;
    private final MemberService memberService;

    // 고수가 충전할 수 있는 판매 중 상품을 가격 오름차순으로 조회한다.
    public List<CreditProductResponse> getCreditProducts(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);
        if (member.getRole() != MemberRole.EXPERT) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        return creditProductRepository.findAllByActiveTrueOrderByPriceAsc().stream()
                .map(product -> new CreditProductResponse(
                        product.getId(), product.getName(), product.getPrice(), product.getCreditAmount()))
                .toList();
    }

    // 주문 생성에서 사용한다. 없거나 판매 중지된 상품이면 예외
    public CreditProduct getActiveProductEntity(Long productId) {
        return creditProductRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CREDIT_PRODUCT_NOT_FOUND_EXCEPTION));
    }
}
