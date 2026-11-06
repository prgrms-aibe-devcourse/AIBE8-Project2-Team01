package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"first_region_id", "name_key"}))
public class SecondRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 150)
    private String nameKey; // 표시 이름을 유지하고 소문자로 통일한 값으로 중복 방지

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "first_region_id", nullable = false)
    private FirstRegion firstRegion;

    @PrePersist
    @PreUpdate
    private void updateNameKey() {
        nameKey = name.toLowerCase();
    }
}
