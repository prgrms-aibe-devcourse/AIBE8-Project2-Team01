package com.grepp.efy.domain.community.post.dto;

import com.grepp.efy.domain.community.post.BoardType;
import lombok.Builder;

// 게시글 목록 검색 조건 (공개 목록, 내 글 목록) (컨트롤러에서 @RequestParam으로 검증 후 서비스로 전달)
@Builder
public record PostSearchCondition(
        BoardType boardType,
        int pageNumber,
        String keyword,
        Long firstCategoryId,
        Long secondCategoryId,
        Long firstRegionId,
        Long secondRegionId
) {
}
