package com.grepp.efy.domain.credit.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CreditProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // 예: 크레딧 10개

    @Column(nullable = false)
    private Long price; // 결제 금액(원)

    @Column(nullable = false)
    private Long creditAmount; // 지급 크레딧 (1크레딧 = 1,000원)

    @Column(name = "is_active", nullable = false)
    private boolean active = true; // 판매 중인 상품만 노출

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

}
