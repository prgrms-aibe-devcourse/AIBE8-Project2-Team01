package com.grepp.efy.domain.user.member;

import com.grepp.efy.domain.user.member.dto.MemberCreateRequest;
import com.grepp.efy.domain.user.member.dto.MemberResponse;
import com.grepp.efy.domain.user.member.dto.MemberPublicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tokenInvalidBefore", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Member toEntity(MemberCreateRequest memberCreateRequest);

    MemberResponse toResponse(Member member);

    MemberPublicResponse toPublicResponse(Member member);
}
