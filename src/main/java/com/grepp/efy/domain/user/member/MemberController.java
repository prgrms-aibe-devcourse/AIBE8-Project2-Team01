package com.grepp.efy.domain.user.member;

import com.grepp.efy.domain.user.expertprofile.ExpertProfileService;
import com.grepp.efy.domain.user.member.docs.MemberControllerDocs;
import com.grepp.efy.domain.user.member.dto.MemberResponse;
import com.grepp.efy.domain.user.member.dto.MemberRoleUpdateRequest;
import com.grepp.efy.domain.user.member.dto.MemberUpdateRequest;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.RefreshTokenCookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final ExpertProfileService expertProfileService;
    private final RefreshTokenCookie refreshTokenCookie;

    @GetMapping("/admin/members")
    @Override
    public ResponseEntity<PageResponse<MemberResponse>> getMembers(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        PageResponse<MemberResponse> response = memberService.getMembers(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/members/me")
    @Override
    public ResponseEntity<MemberResponse> getMyData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberResponse response = memberService.getCurrentMember(userDetails.getMemberId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/members/me")
    @Override
    public ResponseEntity<MemberResponse> updateMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        MemberResponse response = memberService.updateMember(userDetails.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/members/me/role")
    @Override
    public ResponseEntity<MemberResponse> changeMyRole(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRoleUpdateRequest request
    ) {
        MemberResponse response = expertProfileService.changeMyRole(userDetails.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/members/{memberId}/role")
    @Override
    public ResponseEntity<MemberResponse> setRole(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long memberId,
            @Valid @RequestBody MemberRoleUpdateRequest request
    ) {
        MemberResponse response = memberService.setRole(userDetails.getMemberId(), memberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/members/me")
    @Override
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.expire().toString()).build();
    }

    @DeleteMapping("/admin/members/{memberId}")
    @Override
    public ResponseEntity<Void> deleteMemberByAdmin(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
