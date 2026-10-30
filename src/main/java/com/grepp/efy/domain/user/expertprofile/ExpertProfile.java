package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, length = 100)
    private String shortIntro; // 한 줄 소개

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 서비스 상세 설명

    private Integer careerYears; // 경력 연수. 미입력은 null이며 0년과 구분

    @Column(columnDefinition = "TEXT")
    private String careerDescription; // 경력 설명
}
