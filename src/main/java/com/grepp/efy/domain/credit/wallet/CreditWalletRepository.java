package com.grepp.efy.domain.credit.wallet;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CreditWalletRepository extends JpaRepository<CreditWallet, Long> {

    Optional<CreditWallet> findByMemberId(Long memberId);

    // 예약·차감 전에 지갑 행을 잠가 동시 요청으로 잔액이 어긋나지 않게 한다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from CreditWallet w where w.member.id = :memberId")
    Optional<CreditWallet> findByMemberIdForUpdate(@Param("memberId") Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from CreditWallet w where w.id = :id")
    Optional<CreditWallet> findByIdForUpdate(@Param("id") Long id);
}