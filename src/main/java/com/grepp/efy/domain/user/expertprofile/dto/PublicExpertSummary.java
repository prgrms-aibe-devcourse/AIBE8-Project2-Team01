package com.grepp.efy.domain.user.expertprofile.dto;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;

import java.util.Set;

public record PublicExpertSummary(
        Long id,
        String nickname,
        String shortIntro,
        Integer careerYears,
        String imageUrl,
        String representativeWorkImageUrl,
        SecondCategoryResponse primaryCategory,
        boolean nationwide,
        Set<FirstRegionResponse> wholeRegions,
        Set<SecondRegionResponse> regions
) {
}
