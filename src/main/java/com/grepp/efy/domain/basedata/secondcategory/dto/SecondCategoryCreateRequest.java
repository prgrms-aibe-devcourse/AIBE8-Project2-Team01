package com.grepp.efy.domain.basedata.secondcategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SecondCategoryCreateRequest {

    @NotBlank(message = "카테고리 이름을 입력해 주세요.")
    @Size(max = 50, message = "카테고리 이름은 50자 이하로 입력해 주세요.")
    private String name;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
