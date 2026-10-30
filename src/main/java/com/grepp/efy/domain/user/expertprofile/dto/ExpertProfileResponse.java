package com.grepp.efy.domain.user.expertprofile.dto;

import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;

import java.util.Set;

public record ExpertProfileResponse(
        Long id,
        String shortIntro,
        String description,
        Integer careerYears,
        String careerDescription,
        Set<SecondCategoryResponse> services,
        SecondCategoryResponse primaryCategory
) {
}
