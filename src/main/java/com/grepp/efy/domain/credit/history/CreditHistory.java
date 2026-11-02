package com.grepp.efy.domain.credit.history;

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
@Table(uniqueConstraints = {
        // 같은 결제로 두 번 지급·환불하지 않는다
        @UniqueConstraint(name = "uk_credit_history_payment", columnNames = {"history_type", "payment_id"}),
        // 같은 견적서로 두 번 차감하지 않는다
        @UniqueConstraint(name = "uk_credit_history_quote", columnNames = {"history_type", "quote_id"})
})
public class CreditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_wallet_id", nullable = false)
    private CreditWallet creditWallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditHistoryType historyType;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long balanceAfter;

    private Long paymentId;

    private Long quoteId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
