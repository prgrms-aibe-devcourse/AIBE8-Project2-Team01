package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpertProfileMapper {

    ExpertProfile toEntity(ExpertProfileRequest expertProfileRequest);

    ExpertProfileResponse toResponse(ExpertProfile expertProfile);

}
