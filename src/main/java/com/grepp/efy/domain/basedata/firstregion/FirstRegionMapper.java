package com.grepp.efy.domain.basedata.firstregion;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FirstRegionMapper {

    @Mapping(target = "id", ignore = true)
    FirstRegion toEntity(FirstRegionRequest request);

    FirstRegionResponse toResponse(FirstRegion firstRegion);

    List<FirstRegionResponse> toResponseList(List<FirstRegion> firstRegionList);
}
