package com.grepp.efy.domain.credit.reservation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CreditReservationRepository extends JpaRepository<CreditReservation, Long> {

    boolean existsByQuoteId(Long quoteId);

    // 같은 견적서의 차감·해제가 동시에 처리되지 않도록 예약 행을 잠근다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from CreditReservation r where r.quoteId = :quoteId")
    Optional<CreditReservation> findByQuoteIdForUpdate(@Param("quoteId") Long quoteId);

    @Query("select coalesce(sum(r.amount), 0L) from CreditReservation r "
            + "where r.creditWallet.id = :walletId and r.reservationStatus = :status")
    long sumAmountByWalletIdAndStatus(@Param("walletId") Long walletId,
                                      @Param("status") CreditReservationStatus status);

    List<CreditReservation> findAllByCreditWalletIdAndReservationStatusOrderByCreatedAtDescIdDesc(
            Long creditWalletId, CreditReservationStatus reservationStatus);

}