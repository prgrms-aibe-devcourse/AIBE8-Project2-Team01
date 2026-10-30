package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertProfileService {
    private final ExpertProfileRepository expertProfileRepository;
    private final ExpertProfileMapper expertProfileMapper;
    private final MemberService memberService;

    public ExpertProfileResponse getExpertProfile(Long expertProfileId) {
        ExpertProfile expertProfile = getExpertProfileEntity(expertProfileId);
        memberService.getMemberEntity(expertProfile.getMember().getId());
        return expertProfileMapper.toResponse(expertProfile);
    }

    public ExpertProfile getExpertProfileEntity(Long expertProfileId) {
        return expertProfileRepository.findById(expertProfileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }

    public ExpertProfile getExpertProfileByMemberId(Long memberId) {
        return expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }
}
