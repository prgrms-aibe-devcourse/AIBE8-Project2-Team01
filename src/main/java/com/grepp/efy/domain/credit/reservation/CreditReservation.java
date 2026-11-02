package com.grepp.efy.domain.credit.reservation;

import com.grepp.efy.domain.credit.wallet.CreditWallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CreditReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_wallet_id", nullable = false)
    private CreditWallet creditWallet;

    @Column(nullable = false, unique = true)
    private Long quoteId; // 견적서 1건당 예약 1건.

    @Column(nullable = false)
    private Long amount; // 예약 크레딧

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditReservationStatus reservationStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; // USED•RELEASED로 바뀐 시각

    @PrePersist
    public void prePersist() {
        if(this.createdAt == null){
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
