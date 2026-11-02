package com.grepp.efy.domain.user.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberImageUpdateRequest {
    @NotNull(message = "프로필 사진을 선택해 주세요.")
    private Long imageId;
}
