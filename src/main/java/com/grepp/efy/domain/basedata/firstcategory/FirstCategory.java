package com.grepp.efy.domain.basedata.firstcategory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

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

    // DB에서도 대소문자를 구분하지 않는 이름 중복을 막는다.
    @Column(nullable = false, unique = true, length = 50)
    private String normalizedName;

    @PrePersist
    @PreUpdate
    public void normalizeName() {
        this.normalizedName = name.toLowerCase(Locale.ROOT);
    }
}
