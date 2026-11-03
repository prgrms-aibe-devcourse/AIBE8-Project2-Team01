package com.grepp.efy.domain.community.post.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostVisibilityRequest {

    @NotNull(message = "공개 여부를 입력해 주세요.")
    private Boolean isVisible;

    @Size(min = 1, max = 200, message = "사유는 1자 이상 200자 이하로 입력해 주세요.")
    private String reason;

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }
}
