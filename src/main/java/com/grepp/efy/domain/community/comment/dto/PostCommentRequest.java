package com.grepp.efy.domain.community.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class PostCommentRequest {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 2, max = 400, message = "댓글은 2자 이상 400자 이하로 입력해 주세요.")
    private String content;

    @Size(max = 1, message = "이미지는 최대 1개까지 첨부할 수 있습니다.")
    private List<Long> imageIds;

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
}
