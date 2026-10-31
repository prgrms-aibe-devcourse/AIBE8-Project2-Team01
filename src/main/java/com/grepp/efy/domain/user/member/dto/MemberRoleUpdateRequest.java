package com.grepp.efy.domain.user.member.dto;

import com.grepp.efy.domain.user.member.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberRoleUpdateRequest {

    @NotNull(message = "권한을 선택해 주세요.")
    private MemberRole role;

    public void setRole(String role) {
        this.role = role == null ? null : MemberRole.valueOf(role);
    }
}
