package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"first_region_id", "normalized_name"}))
public class SecondRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    // DB에서도 대소문자를 구분하지 않는 이름 중복을 막는다.
    @Column(nullable = false, length = 50)
    private String normalizedName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "first_region_id", nullable = false)
    private FirstRegion firstRegion;

    @PrePersist
    @PreUpdate
    public void normalizeName() {
        this.normalizedName = name.toLowerCase(Locale.ROOT);
    }
}
