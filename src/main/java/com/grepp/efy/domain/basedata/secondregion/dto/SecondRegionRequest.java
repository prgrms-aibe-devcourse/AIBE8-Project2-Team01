package com.grepp.efy.domain.basedata.secondregion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SecondRegionRequest {

    @NotBlank(message = "지역 이름을 입력해 주세요.")
    @Size(max = 50, message = "지역 이름은 50자 이하로 입력해 주세요.")
    private String name;

    @Setter
    @NotNull(message = "상위 항목 ID를 입력해 주세요.")
    @Positive(message = "상위 항목 ID는 양수로 입력해 주세요.")
    private Long firstRegionId;

    @NotBlank(message = "지역 코드를 입력해 주세요.")
    @Pattern(regexp = "[0-9]{10}", message = "지역 코드는 숫자 10자리로 입력해 주세요.")
    private String code;

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
