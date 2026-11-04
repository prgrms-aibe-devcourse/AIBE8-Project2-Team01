package com.grepp.efy.domain.reaction.bookmark.dto;

import com.grepp.efy.domain.community.post.dto.PostResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

// 내 북마크 목록의 한 항목. 카테고리·지역 모양은 게시글 응답과 같다
@Builder
public record BookmarkSummaryResponse(
        Long expertProfileId,
        String expertNickname,
        String expertProfileImageUrl,
        Double averageRating,
        long reviewCount,
        PostResponse.PostCategory category,
        boolean nationwide,
        List<WholeRegion> wholeRegions,
        List<PostResponse.PostRegion> regions,
        LocalDateTime bookmarkedAt
) {
    // 시/도 전체 활동
    public record WholeRegion(Long firstRegionId, String firstRegionName) {
    }
}
