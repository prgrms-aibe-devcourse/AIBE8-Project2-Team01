package com.grepp.efy.domain.user.member;

import com.grepp.efy.domain.user.member.dto.MemberCreateRequest;
import com.grepp.efy.domain.user.member.dto.MemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "image", ignore = true)
    Member toEntity(MemberCreateRequest memberCreateRequest);

    @Mapping(target = "imageId", source = "member.image.id")
    MemberResponse toResponse(Member member, String imageUrl);

}
