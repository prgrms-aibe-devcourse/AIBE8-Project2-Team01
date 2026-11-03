package com.grepp.efy.domain.community.post.dto;

import com.grepp.efy.domain.community.post.BoardType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record PostResponse(
        Long postId,
        BoardType boardType,
        Long memberId,
        String memberNickname,
        String memberProfileImageUrl,
        String title,
        String content,
        List<PostImage> images,
        PostCategory category,
        PostRegion region,
        long commentCount,
        long likeCount,
        boolean liked,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isVisible
) {
    public record PostImage(Long fileId, String url) {
    }

    public record PostCategory(Long firstCategoryId, String firstCategoryName,
                               Long secondCategoryId, String secondCategoryName) {
    }

    public record PostRegion(Long firstRegionId, String firstRegionName,
                             Long secondRegionId, String secondRegionName) {
    }
}
