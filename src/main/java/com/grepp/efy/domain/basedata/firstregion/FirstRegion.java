package com.grepp.efy.domain.basedata.firstregion;

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
public class FirstRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String nameKey; // 표시 이름을 유지하고 소문자로 통일한 값으로 중복 방지

    @PrePersist
    @PreUpdate
    private void updateNameKey() {
        nameKey = name.toLowerCase();
    }
}
