package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionService;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryService;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionService;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.domain.user.member.dto.MemberResponse;
import com.grepp.efy.domain.user.member.dto.MemberRoleUpdateRequest;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertProfileService {
    private final ExpertProfileRepository expertProfileRepository;
    private final ExpertProfileMapper expertProfileMapper;
    private final MemberService memberService;
    private final SecondCategoryService secondCategoryService;
    private final FirstRegionService firstRegionService;
    private final SecondRegionService secondRegionService;

    public ExpertProfileResponse getExpertProfile(Long expertProfileId) {
        ExpertProfile expertProfile = getExpertProfileEntity(expertProfileId);
        memberService.getMemberEntity(expertProfile.getMember().getId());
        return expertProfileMapper.toResponse(expertProfile);
    }

    @Transactional
    public ExpertProfileResponse createExpertProfile(Long memberId, ExpertProfileRequest request) {
        Member member = memberService.getMemberEntity(memberId);
        if (expertProfileRepository.existsByMemberId(memberId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EXPERT_PROFILE_EXCEPTION);
        }
        if (member.getRole() != MemberRole.USER) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        checkServices(request);
        checkRegions(request);

        ExpertProfile expertProfile = expertProfileMapper.toEntity(request);
        expertProfile.setMember(member);
        expertProfile.setNationwide(request.isNationwide());
        for (Long serviceId : request.getServiceIds()) {
            SecondCategory service = secondCategoryService.getSecondCategoryEntity(serviceId);
            expertProfile.getServices().add(service);
            if (serviceId.equals(request.getPrimaryCategoryId())) {
                expertProfile.setPrimaryCategory(service);
            }
        }
        for (Long regionId : request.getWholeRegionIds()) {
            FirstRegion region = firstRegionService.getFirstRegionEntity(regionId);
            expertProfile.getWholeRegions().add(region);
        }
        for (Long regionId : request.getRegionIds()) {
            SecondRegion region = secondRegionService.getSecondRegionEntity(regionId);
            if (request.getWholeRegionIds().contains(region.getFirstRegion().getId())) {
                throw new BusinessException(ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
            }
            expertProfile.getRegions().add(region);
        }

        ExpertProfile savedExpertProfile = expertProfileRepository.save(expertProfile);
        memberService.changeToExpert(memberId);
        return expertProfileMapper.toResponse(savedExpertProfile);
    }

    @Transactional
    public MemberResponse setRole(Long adminId, Long memberId, MemberRoleUpdateRequest request) {
        memberService.getMemberEntity(memberId);
        if (adminId.equals(memberId)) {
            throw new BusinessException(ErrorCode.SELF_ROLE_CHANGE_NOT_ALLOWED_EXCEPTION);
        }
        if (request.getRole() == MemberRole.EXPERT) {
            getExpertProfileByMemberId(memberId);
        }
        return memberService.setRole(adminId, memberId, request);
    }

    @Transactional
    public MemberResponse changeMyRole(Long memberId, MemberRoleUpdateRequest request) {
        Member member = memberService.getMemberEntity(memberId);
        if (member.getRole() == MemberRole.ADMIN || request.getRole() == MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        if (request.getRole() == MemberRole.EXPERT) {
            getExpertProfileByMemberId(memberId);
        }
        return memberService.changeMyRole(memberId, request);
    }

    public ExpertProfile getExpertProfileEntity(Long expertProfileId) {
        return expertProfileRepository.findById(expertProfileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }

    public ExpertProfile getExpertProfileByMemberId(Long memberId) {
        return expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }

    private void checkServices(ExpertProfileRequest request) {
        List<Long> serviceIds = request.getServiceIds();
        if (serviceIds.contains(null)
                || new HashSet<>(serviceIds).size() != serviceIds.size()
                || !serviceIds.contains(request.getPrimaryCategoryId())) {
            throw new BusinessException(ErrorCode.INVALID_EXPERT_SERVICE_EXCEPTION);
        }
    }

    private void checkRegions(ExpertProfileRequest request) {
        List<Long> wholeRegionIds = request.getWholeRegionIds();
        List<Long> regionIds = request.getRegionIds();
        boolean hasRegions = !wholeRegionIds.isEmpty() || !regionIds.isEmpty();
        if (wholeRegionIds.contains(null) || regionIds.contains(null)
                || new HashSet<>(wholeRegionIds).size() != wholeRegionIds.size()
                || new HashSet<>(regionIds).size() != regionIds.size()
                || (request.isNationwide() && hasRegions)
                || (!request.isNationwide() && !hasRegions)) {
            throw new BusinessException(ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
        }
    }

}
