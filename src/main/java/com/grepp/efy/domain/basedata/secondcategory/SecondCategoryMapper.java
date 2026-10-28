package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryCreateRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecondCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstCategory", ignore = true)
    SecondCategory toEntity(SecondCategoryCreateRequest request);

    @Mapping(target = "firstCategoryId", source = "firstCategory.id")
    SecondCategoryResponse toResponse(SecondCategory secondCategory);

    List<SecondCategoryResponse> toResponseList(List<SecondCategory> secondCategoryList);
}
