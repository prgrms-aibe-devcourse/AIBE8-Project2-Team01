package com.grepp.efy.domain.reaction.postlike.dto;

public record PostCommentLikeResponse(
        Long commentId,
        boolean liked,
        long likeCount
) {
}
