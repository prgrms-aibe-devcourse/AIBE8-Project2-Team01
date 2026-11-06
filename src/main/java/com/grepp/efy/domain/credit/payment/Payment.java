package com.grepp.efy.domain.credit.payment;

import com.grepp.efy.domain.credit.product.CreditProduct;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_product_id", nullable = false)
    private CreditProduct creditProduct;

    @Column(nullable = false, unique = true, length = 64)
    private String orderId; // 서버가 생성해 토스에 전달하는 주문번호

    @Column(nullable = false, length = 100)
    private String orderName;

    @Column(nullable = false)
    private Long amount; // 주문 생성 시점의 상품 가격

    @Column(nullable = false)
    private Long creditAmount; // 승인 시 지급할 크레딧

    @Column(unique = true, length = 200)
    private String paymentKey; // 토스 승인 후 저장

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private LocalDateTime approvedAt;

    private LocalDateTime refundedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
