package com.grepp.efy.domain.community.post.dto;

import com.grepp.efy.domain.community.post.BoardType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostSummaryResponse(
        Long postId,
        BoardType boardType,
        String memberNickname,
        String title,
        String thumbnailUrl,
        PostResponse.PostCategory category,
        PostResponse.PostRegion region,
        long commentCount,
        long likeCount,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isVisible
) {

}
