package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryMapper;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SecondCategoryMapper.class)
public interface ExpertProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "primaryCategory", ignore = true)
    ExpertProfile toEntity(ExpertProfileRequest expertProfileRequest);

    ExpertProfileResponse toResponse(ExpertProfile expertProfile);

}
