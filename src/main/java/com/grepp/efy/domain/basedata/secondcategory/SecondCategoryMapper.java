package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecondCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "normalizedName", ignore = true)
    @Mapping(target = "firstCategory", ignore = true)
    SecondCategory toEntity(SecondCategoryRequest request);

    @Mapping(target = "firstCategoryId", source = "firstCategory.id")
    SecondCategoryResponse toResponse(SecondCategory secondCategory);
}
