package com.grepp.efy.domain.credit.wallet;

import com.grepp.efy.domain.credit.CreditAccessValidator;
import com.grepp.efy.domain.credit.reservation.CreditReservationRepository;
import com.grepp.efy.domain.credit.reservation.CreditReservationStatus;
import com.grepp.efy.domain.credit.wallet.dto.CreditBalanceResponse;
import com.grepp.efy.domain.user.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditWalletService {

    // MVP는 카테고리와 관계없이 견적 1건당 2크레딧
    public static final long CREDIT_PER_QUOTE = 2;

    private final CreditWalletRepository creditWalletRepository;
    private final CreditReservationRepository creditReservationRepository;
    private final MemberService memberService;
    private final CreditAccessValidator creditAccessValidator;

    // 고수 본인의 보유·예약 크레딧과 추가 발송 가능한 견적 수를 조회한다. 지갑이 없으면 0으로 응답한다.
    public CreditBalanceResponse getMyCredit(Long memberId) {
        creditAccessValidator.getExpert(memberId);
        return creditWalletRepository.findByMemberId(memberId)
                .map(this::toBalanceResponse)
                .orElseGet(() -> new CreditBalanceResponse(0, 0, CREDIT_PER_QUOTE, 0));
    }

    private CreditBalanceResponse toBalanceResponse(CreditWallet wallet) {
        long reserved = creditReservationRepository.sumAmountByWalletIdAndStatus(
                wallet.getId(), CreditReservationStatus.RESERVED);
        long available = Math.max(wallet.getBalance() - reserved, 0);
        return new CreditBalanceResponse(wallet.getBalance(), reserved, CREDIT_PER_QUOTE, available / CREDIT_PER_QUOTE);
    }
}