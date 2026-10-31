package com.grepp.efy.domain.user.expertprofile.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExpertProfileRequest {
    @NotBlank(message = "한 줄 소개를 입력해 주세요.")
    @Size(max = 100, message = "한 줄 소개는 100자 이내로 입력해 주세요.")
    private String shortIntro;

    @NotBlank(message = "서비스 상세 설명을 입력해 주세요.")
    @Size(min = 10, max = 2000, message = "서비스 상세 설명은 10자 이상 2000자 이하로 입력해 주세요.")
    private String description;

    @Min(value = 0, message = "경력 연수는 0년 이상으로 입력해 주세요.")
    @Max(value = 80, message = "경력 연수는 80년 이하로 입력해 주세요.")
    private Integer careerYears;

    @Size(max = 1000, message = "경력 설명은 1000자 이내로 입력해 주세요.")
    private String careerDescription;

    // 요청에서는 중복을 검사할 수 있도록 List로 받는다.
    @NotEmpty(message = "제공 서비스를 하나 이상 선택해 주세요.")
    private List<Long> serviceIds;

    @NotNull(message = "대표 서비스를 선택해 주세요.")
    private Long primaryCategoryId;

    private boolean nationwide = false;

    @NotNull(message = "시도 전체 선택 정보가 올바르지 않습니다.")
    private List<Long> wholeRegionIds = new ArrayList<>();

    @NotNull(message = "시군구 선택 정보가 올바르지 않습니다.")
    private List<Long> regionIds = new ArrayList<>();

    // intValueExact() : 소수 부분이 있거나 int 범위를 벗어나면 예외를 발생
    public void setCareerYears(BigDecimal careerYears) {
        this.careerYears = careerYears == null ? null : careerYears.intValueExact();
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro == null ? null : shortIntro.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}
