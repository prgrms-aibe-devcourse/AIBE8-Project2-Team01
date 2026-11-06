package com.grepp.efy.domain.user.expertprofile.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkImageRequest {
    @NotNull(message = "작업 사진을 선택해 주세요.")
    private Long fileId;

    @Size(max = 200, message = "작업 사진 설명은 200자 이내로 입력해 주세요.")
    private String description;

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}
