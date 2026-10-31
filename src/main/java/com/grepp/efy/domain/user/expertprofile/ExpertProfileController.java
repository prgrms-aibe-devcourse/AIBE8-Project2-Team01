package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.expertprofile.docs.ExpertProfileControllerDocs;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileRequest;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ExpertProfileController implements ExpertProfileControllerDocs {
    private final ExpertProfileService expertProfileService;

    @GetMapping("/public/expert-profiles/{expertProfileId}")
    @Override
    public ResponseEntity<ExpertProfileResponse> getExpertProfile(@PathVariable Long expertProfileId) {
        ExpertProfileResponse response = expertProfileService.getExpertProfile(expertProfileId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/members/me/expert-profile")
    @Override
    public ResponseEntity<ExpertProfileResponse> createExpertProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ExpertProfileRequest request) {
        ExpertProfileResponse response = expertProfileService.createExpertProfile(userDetails.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
