package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.firstcategory.FirstCategory;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"first_category_id", "name_key"}))
public class SecondCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 150)
    private String nameKey; // 표시 이름은 유지하고 소문자로 통일한 값에 중복 제약 적용

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "first_category_id", nullable = false)
    private FirstCategory firstCategory;
    @PrePersist
    @PreUpdate
    private void updateNameKey() {
        nameKey = name.toLowerCase();
    }
}
