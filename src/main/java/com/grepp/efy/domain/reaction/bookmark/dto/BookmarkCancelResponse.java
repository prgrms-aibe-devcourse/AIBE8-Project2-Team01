package com.grepp.efy.domain.reaction.bookmark.dto;

public record BookmarkCancelResponse(
        Long expertProfileId,
        boolean bookmarked
) {
}
