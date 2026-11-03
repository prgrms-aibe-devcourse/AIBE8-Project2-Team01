package com.grepp.efy.domain.community.post.dto;

public record PostVisibilityResponse(
        Long postId,
        boolean isVisible
) {
}
