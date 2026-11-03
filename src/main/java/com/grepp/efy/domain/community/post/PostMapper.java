package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.community.post.dto.PostSummaryResponse;
import com.grepp.efy.domain.community.post.dto.PostVisibilityHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "memberId", source = "post.member.id")
    @Mapping(target = "memberNickname", source = "post.member.nickname")
    @Mapping(target = "memberProfileImageUrl", source = "memberProfileImageUrl")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "category", source = "post.secondCategory")
    @Mapping(target = "region", source = "post.secondRegion")
    @Mapping(target = "commentCount", source = "post.commentCount")
    @Mapping(target = "likeCount", source = "post.likeCount")
    @Mapping(target = "isVisible", source = "post.visible")
    PostResponse toResponse(Post post, List<PostResponse.PostImage> images, String memberProfileImageUrl, boolean liked);

    @Mapping(target = "firstCategoryId", source = "firstCategory.id")
    @Mapping(target = "firstCategoryName", source = "firstCategory.name")
    @Mapping(target = "secondCategoryId", source = "id")
    @Mapping(target = "secondCategoryName", source = "name")
    PostResponse.PostCategory toCategory(SecondCategory secondCategory);

    @Mapping(target = "firstRegionId", source = "firstRegion.id")
    @Mapping(target = "firstRegionName", source = "firstRegion.name")
    @Mapping(target = "secondRegionId", source = "id")
    @Mapping(target = "secondRegionName", source = "name")
    PostResponse.PostRegion toRegion(SecondRegion secondRegion);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "memberNickname", source = "post.member.nickname")
    @Mapping(target = "category", source = "post.secondCategory")
    @Mapping(target = "region", source = "post.secondRegion")
    @Mapping(target = "commentCount", source = "post.commentCount")
    @Mapping(target = "likeCount", source = "post.likeCount")
    @Mapping(target = "isVisible", source = "post.visible")
    PostSummaryResponse toSummary(Post post, String thumbnailUrl);

    @Mapping(target = "historyId", source = "id")
    @Mapping(target = "isVisible", source = "visible")
    @Mapping(target = "adminId", source = "admin.id")
    @Mapping(target = "adminNickname", source = "admin.nickname")
    PostVisibilityHistoryResponse.History toHistory(PostVisibilityHistory history);
}
