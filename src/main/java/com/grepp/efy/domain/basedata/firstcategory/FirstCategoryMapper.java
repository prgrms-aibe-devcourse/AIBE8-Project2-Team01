package com.grepp.efy.domain.basedata.firstcategory;

import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryRequest;
import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FirstCategoryMapper {

    @Mapping(target = "id", ignore = true)
    FirstCategory toEntity(FirstCategoryRequest request);

    FirstCategoryResponse toResponse(FirstCategory firstCategory);

    List<FirstCategoryResponse> toResponseList(List<FirstCategory> firstCategoryList);
}
