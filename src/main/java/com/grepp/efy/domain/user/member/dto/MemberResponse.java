package com.grepp.efy.domain.user.member.dto;

import com.grepp.efy.domain.user.member.MemberRole;

public record MemberResponse(
        Long id,
        String email,
        String nickname,
        String phone,
        MemberRole role,
        Long imageId,
        String imageUrl
) {}
