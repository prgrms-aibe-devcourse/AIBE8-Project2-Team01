package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionService;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryService;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionService;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileCreateRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileUpdateRequest;
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
    public ExpertProfileResponse createExpertProfile(Long memberId, ExpertProfileCreateRequest createRequest) {
        Member member = memberService.getMemberEntity(memberId);
        if (expertProfileRepository.existsByMemberId(memberId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EXPERT_PROFILE_EXCEPTION);
        }
        if (member.getRole() != MemberRole.USER) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        checkIds(createRequest);

        ExpertProfile expertProfile = expertProfileMapper.toEntity(createRequest);
        expertProfile.setMember(member);
        setServicesAndRegions(
                expertProfile,
                createRequest.getServiceIds(),
                createRequest.getPrimaryCategoryId(),
                createRequest.isNationwide(),
                createRequest.getWholeRegionIds(),
                createRequest.getRegionIds()
        );
        checkProfile(expertProfile);

        ExpertProfile savedExpertProfile = expertProfileRepository.save(expertProfile);
        memberService.changeToExpert(memberId);
        return expertProfileMapper.toResponse(savedExpertProfile);
    }

    @Transactional
    public ExpertProfileResponse updateExpertProfile(Long memberId, ExpertProfileUpdateRequest UpdateRequest) {
        Member member = memberService.getMemberEntity(memberId);
        if (member.getRole() != MemberRole.USER && member.getRole() != MemberRole.EXPERT) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        ExpertProfile expertProfile = getExpertProfileByMemberId(memberId);
        checkIds(UpdateRequest);

        updateInfo(expertProfile, UpdateRequest);
        setServicesAndRegions(
                expertProfile,
                UpdateRequest.getServiceIds(),
                UpdateRequest.getPrimaryCategoryId(),
                UpdateRequest.getNationwide(),
                UpdateRequest.getWholeRegionIds(),
                UpdateRequest.getRegionIds()
        );
        checkProfile(expertProfile);
        return expertProfileMapper.toResponse(expertProfile);
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

    // 프로필 ID로 프로필을 조회하고 없으면 EXPERT_PROFILE_NOT_FOUND_EXCEPTION 발생
    public ExpertProfile getExpertProfileEntity(Long expertProfileId) {
        return expertProfileRepository.findById(expertProfileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }

    // 회원 ID로 프로필을 조회하고 없으면 EXPERT_PROFILE_NOT_FOUND_EXCEPTION 발생
    public ExpertProfile getExpertProfileByMemberId(Long memberId) {
        return expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }

    // 전달된 소개·경력만 수정하고 경력에 명시적으로 null을 보내면 삭제
    private void updateInfo(ExpertProfile expertProfile, ExpertProfileUpdateRequest request) {
        if (request.getShortIntro() != null) {
            if (request.getShortIntro().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION);
            }
            expertProfile.setShortIntro(request.getShortIntro());
        }
        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION);
            }
            expertProfile.setDescription(request.getDescription());
        }
        if (request.isCareerYearsSet()) {
            expertProfile.setCareerYears(request.getCareerYears());
        }
        if (request.isCareerDescriptionSet()) {
            expertProfile.setCareerDescription(request.getCareerDescription());
        }
    }

    // 서비스·대표 서비스·전국 여부·지역을 함께 연결. null은 유지하고 빈 목록은 해당 연결 삭제
    private void setServicesAndRegions(ExpertProfile expertProfile, List<Long> serviceIds,
                                       Long primaryCategoryId, Boolean nationwide,
                                       List<Long> wholeRegionIds, List<Long> regionIds) {
        if (serviceIds != null) {
            expertProfile.getServices().clear();
            for (Long serviceId : serviceIds) {
                SecondCategory service = secondCategoryService.getSecondCategoryEntity(serviceId);
                expertProfile.getServices().add(service);
            }
        }
        if (primaryCategoryId != null) {
            expertProfile.setPrimaryCategory(secondCategoryService.getSecondCategoryEntity(primaryCategoryId));
        }
        if (nationwide != null) {
            expertProfile.setNationwide(nationwide);
        }
        if (wholeRegionIds != null) {
            expertProfile.getWholeRegions().clear();
            for (Long regionId : wholeRegionIds) {
                FirstRegion region = firstRegionService.getFirstRegionEntity(regionId);
                expertProfile.getWholeRegions().add(region);
            }
        }
        if (regionIds != null) {
            expertProfile.getRegions().clear();
            for (Long regionId : regionIds) {
                SecondRegion region = secondRegionService.getSecondRegionEntity(regionId);
                expertProfile.getRegions().add(region);
            }
        }
    }

    // Set에 넣기 전에 목록의 null 원소와 중복 ID 검사
    private void checkDuplicateIds(List<Long> ids, ErrorCode errorCode) {
        if (ids != null && (ids.contains(null) || new HashSet<>(ids).size() != ids.size())) {
            throw new BusinessException(errorCode);
        }
    }

    // 등록 요청의 서비스·지역 목록 검사
    private void checkIds(ExpertProfileCreateRequest request) {
        checkDuplicateIds(request.getServiceIds(), ErrorCode.INVALID_EXPERT_SERVICE_EXCEPTION);
        checkDuplicateIds(request.getWholeRegionIds(), ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
        checkDuplicateIds(request.getRegionIds(), ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
    }

    // 수정 요청의 서비스·지역 목록 검사. 생략한 목록은 유지
    private void checkIds(ExpertProfileUpdateRequest request) {
        checkDuplicateIds(request.getServiceIds(), ErrorCode.INVALID_EXPERT_SERVICE_EXCEPTION);
        checkDuplicateIds(request.getWholeRegionIds(), ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
        checkDuplicateIds(request.getRegionIds(), ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
    }

    // 반영된 프로필의 대표 서비스와 활동 지역 조건 검사
    private void checkProfile(ExpertProfile expertProfile) {
        if (expertProfile.getServices().isEmpty()
                || !expertProfile.getServices().contains(expertProfile.getPrimaryCategory())) {
            throw new BusinessException(ErrorCode.INVALID_EXPERT_SERVICE_EXCEPTION);
        }
        boolean hasRegions = !expertProfile.getWholeRegions().isEmpty() || !expertProfile.getRegions().isEmpty();
        if ((expertProfile.isNationwide() && hasRegions) || (!expertProfile.isNationwide() && !hasRegions)) {
            throw new BusinessException(ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
        }
        for (SecondRegion region : expertProfile.getRegions()) {
            if (expertProfile.getWholeRegions().contains(region.getFirstRegion())) {
                throw new BusinessException(ErrorCode.INVALID_EXPERT_REGION_EXCEPTION);
            }
        }
    }

}
