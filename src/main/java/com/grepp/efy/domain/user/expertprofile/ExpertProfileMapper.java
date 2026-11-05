package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.basedata.firstregion.FirstRegionMapper;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryMapper;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionMapper;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileCreateRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.domain.user.expertprofile.dto.WorkImageResponse;
import com.grepp.efy.domain.user.expertprofile.dto.PublicExpertSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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
    @Mapping(target = "workImages", ignore = true)
    ExpertProfile toEntity(ExpertProfileCreateRequest expertProfileRequest);

    @Mapping(target = "nickname", source = "expertProfile.member.nickname")
    PublicExpertSummary toPublicSummary(ExpertProfile expertProfile, String imageUrl, String representativeWorkImageUrl);

    @Mapping(target = "nickname", source = "expertProfile.member.nickname")
    @Mapping(target = "imageId", source = "expertProfile.member.image.id")
    @Mapping(target = "workImages", source = "workImages")
    ExpertProfileResponse toResponse(ExpertProfile expertProfile, String imageUrl, List<WorkImageResponse> workImages);

}
