package com.grepp.efy.domain.basedata.secondcategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SecondCategoryRequest {

    @NotBlank(message = "카테고리 이름을 입력해 주세요.")
    @Size(max = 50, message = "카테고리 이름은 50자 이하로 입력해 주세요.")
    private String name;

    @Setter
    @NotNull(message = "상위 항목 ID를 입력해 주세요.")
    @Positive(message = "상위 항목 ID는 양수로 입력해 주세요.")
    private Long firstCategoryId;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
