package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.basedata.firstregion.FirstRegionMapper;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryMapper;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionMapper;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                SecondCategoryMapper.class,
                FirstRegionMapper.class,
                SecondRegionMapper.class
        }
)
public interface ExpertProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "primaryCategory", ignore = true)
    @Mapping(target = "nationwide", ignore = true)
    @Mapping(target = "wholeRegions", ignore = true)
    @Mapping(target = "regions", ignore = true)
    ExpertProfile toEntity(ExpertProfileRequest expertProfileRequest);

    ExpertProfileResponse toResponse(ExpertProfile expertProfile);

}
