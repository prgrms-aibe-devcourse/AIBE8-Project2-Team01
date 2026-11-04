package com.grepp.efy.domain.reaction.bookmark.dto;

import java.time.LocalDateTime;

public record BookmarkResponse(
        Long expertProfileId,
        boolean bookmarked,
        LocalDateTime bookmarkedAt
) {
}
