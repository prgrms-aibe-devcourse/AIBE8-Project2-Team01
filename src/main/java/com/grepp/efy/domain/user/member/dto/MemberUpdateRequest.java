package com.grepp.efy.domain.user.member.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {

    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해 주세요.")
    private String password;

    @Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하로 입력해 주세요.")
    private String nickname;

    @Pattern(regexp = "[0-9]{11}", message = "전화번호는 숫자 11자리로 입력해 주세요.")
    private String phone;

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }
}
