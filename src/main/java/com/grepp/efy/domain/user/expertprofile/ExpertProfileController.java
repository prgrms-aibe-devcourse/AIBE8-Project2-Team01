package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.expertprofile.docs.ExpertProfileControllerDocs;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import lombok.RequiredArgsConstructor;
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
}
