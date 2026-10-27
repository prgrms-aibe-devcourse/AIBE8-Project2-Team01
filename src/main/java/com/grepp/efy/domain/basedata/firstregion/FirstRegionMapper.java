package com.grepp.efy.domain.basedata.firstregion;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FirstRegionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "normalizedName", ignore = true)
    FirstRegion toEntity(FirstRegionRequest request);

    FirstRegionResponse toResponse(FirstRegion firstRegion);
}
