package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecondRegionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "normalizedName", ignore = true)
    @Mapping(target = "firstRegion", ignore = true)
    SecondRegion toEntity(SecondRegionRequest request);

    @Mapping(target = "firstRegionId", source = "firstRegion.id")
    SecondRegionResponse toResponse(SecondRegion secondRegion);
}
