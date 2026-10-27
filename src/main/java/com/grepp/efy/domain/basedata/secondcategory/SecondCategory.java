package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.firstcategory.FirstCategory;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"first_category_id", "normalized_name"}))
public class SecondCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    // DB에서도 대소문자를 구분하지 않는 이름 중복을 막는다.
    @Column(nullable = false, length = 50)
    private String normalizedName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "first_category_id", nullable = false)
    private FirstCategory firstCategory;

    @PrePersist
    @PreUpdate
    public void normalizeName() {
        this.normalizedName = name.toLowerCase(Locale.ROOT);
    }
}
