package com.grepp.efy.domain.basedata.firstcategory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirstCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String nameKey; // 표시 이름은 유지하고 소문자로 통일한 값에 중복 제약 적용

    @PrePersist
    @PreUpdate
    private void updateNameKey() {
        nameKey = name.toLowerCase();
    }
}
