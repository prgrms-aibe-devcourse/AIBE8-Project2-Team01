package com.grepp.efy.domain.credit.reservation;

import com.grepp.efy.domain.credit.history.CreditHistory;
import com.grepp.efy.domain.credit.history.CreditHistoryRepository;
import com.grepp.efy.domain.credit.history.CreditHistoryType;
import com.grepp.efy.domain.credit.wallet.CreditWallet;
import com.grepp.efy.domain.credit.wallet.CreditWalletRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.grepp.efy.domain.credit.wallet.CreditWalletService.CREDIT_PER_QUOTE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditReservationService {

    private final CreditWalletRepository creditWalletRepository;
    private final CreditReservationRepository creditReservationRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    // 견적 발송 시 견적 저장과 같은 트랜잭션에서 호출한다.
    // 지갑을 잠근 뒤 사용 가능 크레딧(보유 - 예약)이 2 이상이면 견적서 1건에 2크레딧을 예약한다.
    @Transactional
    public void reserveForQuote(Long memberId, Long quoteId) {
        CreditWallet wallet = creditWalletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSUFFICIENT_CREDIT_EXCEPTION));
        if (creditReservationRepository.existsByQuoteId(quoteId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CREDIT_RESERVATION_EXCEPTION);
        }
        long reserved = creditReservationRepository.sumAmountByWalletIdAndStatus(
                wallet.getId(), CreditReservationStatus.RESERVED);
        if (wallet.getBalance() - reserved < CREDIT_PER_QUOTE) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_CREDIT_EXCEPTION);
        }

        CreditReservation reservation = new CreditReservation();
        reservation.setCreditWallet(wallet);
        reservation.setQuoteId(quoteId);
        reservation.setAmount(CREDIT_PER_QUOTE);
        reservation.setReservationStatus(CreditReservationStatus.RESERVED);
        creditReservationRepository.save(reservation);
    }

    // 의뢰 요청 시 채팅방 생성 전에 같은 트랜잭션에서 호출한다.
    // 예약을 사용 처리하고 잔액에서 차감한 뒤 USE 내역을 남긴다. 이미 사용·해제된 예약이면 거절한다.
    @Transactional
    public void chargeForConnection(Long quoteId) {
        CreditReservation reservation = creditReservationRepository.findByQuoteIdForUpdate(quoteId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CREDIT_RESERVATION_NOT_FOUND_EXCEPTION));
        if (reservation.getReservationStatus() != CreditReservationStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_CREDIT_RESERVATION_STATUS_EXCEPTION);
        }
        CreditWallet wallet = creditWalletRepository.findByIdForUpdate(reservation.getCreditWallet().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CREDIT_RESERVATION_NOT_FOUND_EXCEPTION));
        if (wallet.getBalance() < reservation.getAmount()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_CREDIT_EXCEPTION);
        }

        long balanceAfter = wallet.getBalance() - reservation.getAmount();
        wallet.setBalance(balanceAfter);
        reservation.setReservationStatus(CreditReservationStatus.USED);

        CreditHistory history = new CreditHistory();
        history.setCreditWallet(wallet);
        history.setHistoryType(CreditHistoryType.USE);
        history.setAmount(-reservation.getAmount());
        history.setBalanceAfter(balanceAfter);
        history.setQuoteId(quoteId);
        creditHistoryRepository.save(history);
    }

    // 요청서 취소·견적 만료 시 호출한다. 예약 중인 크레딧만 해제한다.
    // 예약이 없거나 이미 사용·해제된 견적서는 그대로 두므로 여러 번 호출해도 안전하다.
    @Transactional
    public void releaseReservation(Long quoteId) {
        creditReservationRepository.findByQuoteIdForUpdate(quoteId)
                .filter(reservation -> reservation.getReservationStatus() == CreditReservationStatus.RESERVED)
                .ifPresent(reservation -> reservation.setReservationStatus(CreditReservationStatus.RELEASED));
    }

    // 요청서 취소처럼 여러 견적서를 한 번에 해제할 때 사용한다
    @Transactional
    public void releaseReservations(List<Long> quoteIds) {
        quoteIds.forEach(this::releaseReservation);
    }
}