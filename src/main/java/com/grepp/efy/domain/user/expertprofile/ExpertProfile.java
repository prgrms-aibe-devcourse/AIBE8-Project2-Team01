package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExpertProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 100)
    private String shortIntro; // 한 줄 소개

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 서비스 상세 설명

    private Integer careerYears; // 경력 연수. 미입력은 null이며 0년과 구분

    @Column(columnDefinition = "TEXT")
    private String careerDescription; // 경력 설명

    @ManyToMany
    private Set<SecondCategory> services = new HashSet<>(); // 제공 서비스 목록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_category_id", nullable = false)
    private SecondCategory primaryCategory; // 대표 서비스

    @Column(name = "is_nationwide", nullable = false)
    private boolean nationwide = false; // 전국 활동 여부

    @ManyToMany
    private Set<FirstRegion> wholeRegions = new HashSet<>(); // 전체 활동을 선택한 시도

    @ManyToMany
    private Set<SecondRegion> regions = new HashSet<>(); // 개별 선택한 시군구
    // 프로필에서 연결을 제거하면 작업 사진 정보만 삭제하고 업로드 파일은 유지한다.
    @OneToMany(mappedBy = "expertProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<WorkImage> workImages = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
