package com.grepp.efy.domain.community.post.dto;

import com.grepp.efy.domain.community.post.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequest {

    private Long secondCategoryId;

    private Long secondRegionId;

    @NotNull(message = "게시판 종류를 선택해 주세요.")
    private BoardType boardType;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 2, max = 30, message = "제목은 2자 이상 30자 이하로 입력해 주세요.")
    private String title;

    @NotBlank(message = "본문은 필수입니다.")
    @Size(min = 2, max = 5000, message = "본문은 2자 이상 5000자 이하로 입력해 주세요.")
    private String content;

    @Size(max = 10, message = "이미지는 최대 10개까지 첨부할 수 있습니다.")
    private List<Long> imageIds;

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}
