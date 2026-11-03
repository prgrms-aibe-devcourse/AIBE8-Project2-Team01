package com.grepp.efy.domain.credit.wallet;

import com.grepp.efy.domain.credit.CreditAccessValidator;
import com.grepp.efy.domain.credit.history.CreditHistory;
import com.grepp.efy.domain.credit.history.CreditHistoryRepository;
import com.grepp.efy.domain.credit.history.CreditHistoryType;
import com.grepp.efy.domain.credit.reservation.CreditReservationRepository;
import com.grepp.efy.domain.credit.reservation.CreditReservationStatus;
import com.grepp.efy.domain.credit.wallet.dto.CreditBalanceResponse;
import com.grepp.efy.domain.user.member.Member;
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
    private final CreditAccessValidator creditAccessValidator;
    private final CreditHistoryRepository creditHistoryRepository;

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

    // 결제 승인 시 호출한다. 지갑이 없으면 만들고, 크레딧을 지급한 뒤 CHARGE 내역을 남긴다
    @Transactional
    public long chargeForPayment(Member member, Long paymentId, long creditAmount) {
        CreditWallet wallet = creditWalletRepository.findByMemberIdForUpdate(member.getId())
                .orElseGet(() -> {
                    CreditWallet newWallet = new CreditWallet();
                    newWallet.setMember(member);
                    return creditWalletRepository.save(newWallet);
                });

        long balanceAfter = wallet.getBalance() + creditAmount;
        wallet.setBalance(balanceAfter);

        CreditHistory history = new CreditHistory();
        history.setCreditWallet(wallet);
        history.setHistoryType(CreditHistoryType.CHARGE);
        history.setAmount(creditAmount);
        history.setBalanceAfter(balanceAfter);
        history.setPaymentId(paymentId); // (CHARGE, payment_id) 유니크로 이중 지급을 DB가 한 번 더 막는다
        creditHistoryRepository.save(history);
        return balanceAfter;
    }

    // 현재 보유 크레딧. 지갑이 없으면 0
    public long getBalance(Long memberId) {
        return creditWalletRepository.findByMemberId(memberId)
                .map(CreditWallet::getBalance)
                .orElse(0L);
    }
}