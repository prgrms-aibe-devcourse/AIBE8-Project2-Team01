package com.grepp.efy.domain.community.comment.dto;

import com.grepp.efy.domain.community.post.dto.PostResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostCommentResponse(
        Long commentId,
        Long memberId,
        String memberNickname,
        String memberProfileImageUrl,
        boolean isExpert,
        String content,
        List<PostResponse.PostImage> images,
        long likeCount,
        boolean liked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
