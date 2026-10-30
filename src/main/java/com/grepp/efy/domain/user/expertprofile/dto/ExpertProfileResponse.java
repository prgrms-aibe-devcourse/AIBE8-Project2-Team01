package com.grepp.efy.domain.user.expertprofile.dto;

public record ExpertProfileResponse(
        Long id,
        String shortIntro,
        String description,
        Integer careerYears,
        String careerDescription
) {
}
