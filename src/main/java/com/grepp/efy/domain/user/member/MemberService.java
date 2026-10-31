package com.grepp.efy.domain.user.member;

import com.grepp.efy.domain.user.auth.RefreshTokenService;
import com.grepp.efy.domain.user.member.dto.MemberCreateRequest;
import com.grepp.efy.domain.user.member.dto.MemberResponse;
import com.grepp.efy.domain.user.member.dto.MemberRoleUpdateRequest;
import com.grepp.efy.domain.user.member.dto.MemberUpdateRequest;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final List<String> SORTABLE_FIELDS = List.of("id", "nickname", "email", "createdAt");
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final RefreshTokenService refreshTokenService;

    public PageResponse<MemberResponse> getMembers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Member> page = memberRepository.findByDeletedAtIsNull(pageable);
        return PageResponse.of(page, page.getContent().stream().map(memberMapper::toResponse).toList());
    }

    public MemberResponse getCurrentMember(Long memberId) {
        Member member = getMemberEntity(memberId);
        return memberMapper.toResponse(member);
    }

    @Transactional
    public Member create(MemberCreateRequest memberCreateRequest) {

        ensureNotReserved(memberCreateRequest.getEmail(), memberCreateRequest.getNickname());
        if (memberRepository.existsByPhone(memberCreateRequest.getPhone())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_EXCEPTION);
        }

        // 중복된 이메일과 닉네임은 가입을 거절한다.
        if (memberRepository.existsByEmail(memberCreateRequest.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL_EXCEPTION);
        }
        if (memberRepository.existsByNickname(memberCreateRequest.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
        }

        Member member = memberMapper.toEntity(memberCreateRequest);

        // 비밀번호는 해시로 저장하고 가입 권한은 서버에서 정한다.
        member.setPassword(passwordEncoder.encode(memberCreateRequest.getPassword()));
        // 가입 시 일반 사용자 권한을 준다.
        member.setRole(MemberRole.USER);
        return memberRepository.save(member);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = getMemberEntity(memberId);

        if (request.getNickname() != null) {
            ensureNotReserved(null, request.getNickname());
            if (memberRepository.existsByNicknameAndIdNot(request.getNickname(), memberId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
            }
            member.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            if (memberRepository.existsByPhoneAndIdNot(request.getPhone(), memberId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_PHONE_EXCEPTION);
            }
            member.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return memberMapper.toResponse(member);
    }

    @Transactional
    public MemberResponse setRole(Long adminId, Long memberId, MemberRoleUpdateRequest request) {
        Member member = getMemberEntity(memberId);
        if (adminId.equals(memberId)) {
            throw new BusinessException(ErrorCode.SELF_ROLE_CHANGE_NOT_ALLOWED_EXCEPTION);
        }

        if (member.getRole() != request.getRole()) {
            member.setRole(request.getRole());
        }
        return memberMapper.toResponse(member);
    }

    // 프로필 조건은 ExpertProfileService에서 확인하고 회원 역할만 변경한다.
    @Transactional
    public MemberResponse changeMyRole(Long memberId, MemberRoleUpdateRequest request) {
        Member member = getMemberEntity(memberId);
        if (member.getRole() == MemberRole.ADMIN || request.getRole() == MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        if (member.getRole() != request.getRole()) {
            member.setRole(request.getRole());
        }
        return memberMapper.toResponse(member);
    }

    // 첫 프로필 등록 시 일반 회원을 고수로 전환한다.
    @Transactional
    public void changeToExpert(Long memberId) {
        Member member = getMemberEntity(memberId);
        if (member.getRole() != MemberRole.USER) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        member.setRole(MemberRole.EXPERT);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = getMemberEntity(memberId);
        member.setDeletedAt(LocalDateTime.now());
        member.setNickname("탈퇴한회원_" + memberId);
        member.setEmail("deleted_" + memberId + "@deleted.local");
        member.setPhone(null);
        member.setPassword(null);
        invalidateTokens(member);
    }

    // 탈퇴 회원은 제외하고 없으면 MEMBER_NOT_FOUND_EXCEPTION 발생
    public Member getMemberEntity(Long memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
    }

    // 탈퇴 회원은 제외하고, 없으면 MEMBER_NOT_FOUND_EXCEPTION 발생
    public Member getMemberEntityByEmail(String email) {
        return memberRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
    }

    private void invalidateTokens(Member member) {
        refreshTokenService.deleteByMemberId(member.getId());
        member.setTokenVersion(member.getTokenVersion() + 1);
    }

    private void ensureNotReserved(String email, String nickname) {
        if ((email != null && email.endsWith("@deleted.local"))
                || (nickname != null && nickname.startsWith("탈퇴한회원_"))) {
            throw new BusinessException(ErrorCode.RESERVED_MEMBER_VALUE_EXCEPTION);
        }
    }

}
