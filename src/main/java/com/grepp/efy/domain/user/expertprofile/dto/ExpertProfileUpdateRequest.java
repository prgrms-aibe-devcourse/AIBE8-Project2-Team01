package com.grepp.efy.domain.user.expertprofile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ExpertProfileUpdateRequest {
    @Size(min = 1, max = 100, message = "한 줄 소개는 1자 이상 100자 이하로 입력해 주세요.")
    private String shortIntro;

    @Size(min = 10, max = 2000, message = "서비스 상세 설명은 10자 이상 2000자 이하로 입력해 주세요.")
    private String description;

    @Min(value = 0, message = "경력 연수는 0년 이상으로 입력해 주세요.")
    @Max(value = 80, message = "경력 연수는 80년 이하로 입력해 주세요.")
    private Integer careerYears;

    @Size(max = 1000, message = "경력 설명은 1000자 이내로 입력해 주세요.")
    private String careerDescription;

    @Size(min = 1, message = "제공 서비스를 하나 이상 선택해 주세요.")
    private List<Long> serviceIds;
    private Long primaryCategoryId;
    private Boolean nationwide;
    private List<Long> wholeRegionIds;
    private List<Long> regionIds;

    @Valid
    @Size(max = 10, message = "작업 사진은 최대 10장까지 선택해 주세요.")
    private List<@NotNull(message = "작업 사진 선택 정보가 올바르지 않습니다.") WorkImageRequest> workImages;


    // 경력은 생략하면 유지하고 명시적으로 null을 보내면 삭제한다.
    @JsonIgnore
    private boolean careerYearsSet;
    @JsonIgnore
    private boolean careerDescriptionSet;

    public void setCareerYears(BigDecimal careerYears) {
        this.careerYears = careerYears == null ? null : careerYears.intValueExact();
        this.careerYearsSet = true;
    }

    public void setCareerDescription(String careerDescription) {
        this.careerDescription = careerDescription;
        this.careerDescriptionSet = true;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro == null ? null : shortIntro.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}
