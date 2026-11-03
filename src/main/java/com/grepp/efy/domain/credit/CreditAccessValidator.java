package com.grepp.efy.domain.credit;

import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 크레딧·결제 API는 고수(EXPERT)만 이용한다
@Component
@RequiredArgsConstructor
public class CreditAccessValidator {

    private final MemberService memberService;

    public Member getExpert(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);
        if (member.getRole() != MemberRole.EXPERT) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        return member;
    }
}
