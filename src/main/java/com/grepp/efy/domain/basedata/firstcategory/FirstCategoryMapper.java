package com.grepp.efy.domain.basedata.firstcategory;

import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryRequest;
import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FirstCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "normalizedName", ignore = true)
    FirstCategory toEntity(FirstCategoryRequest request);

    FirstCategoryResponse toResponse(FirstCategory firstCategory);
}
