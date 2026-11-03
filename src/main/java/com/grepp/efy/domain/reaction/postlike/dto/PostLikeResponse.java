package com.grepp.efy.domain.reaction.postlike.dto;

public record PostLikeResponse(
        Long postId,
        boolean liked,
        long likeCount
) {
}
