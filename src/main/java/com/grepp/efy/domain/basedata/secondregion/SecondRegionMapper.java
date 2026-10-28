package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionCreateRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecondRegionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstRegion", ignore = true)
    SecondRegion toEntity(SecondRegionCreateRequest request);

    @Mapping(target = "firstRegionId", source = "firstRegion.id")
    SecondRegionResponse toResponse(SecondRegion secondRegion);

    List<SecondRegionResponse> toResponseList(List<SecondRegion> secondRegionList);
}
