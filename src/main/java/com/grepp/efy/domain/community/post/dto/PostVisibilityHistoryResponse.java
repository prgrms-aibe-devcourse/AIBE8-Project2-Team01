package com.grepp.efy.domain.community.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostVisibilityHistoryResponse(
        Long postId,
        List<History> histories
) {

    public record History(
            Long historyId,
            boolean isVisible,
            String reason,
            Long adminId,
            String adminNickname,
            LocalDateTime createdAt
    ) {
    }
}
