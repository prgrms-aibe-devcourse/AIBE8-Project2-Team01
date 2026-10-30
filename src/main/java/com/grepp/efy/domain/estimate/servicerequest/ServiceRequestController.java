package com.grepp.efy.domain.estimate.servicerequest;

import com.grepp.efy.domain.estimate.servicerequest.docs.ServiceRequestControllerDocs;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestCreateRequest;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestCreateResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestListResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ServiceRequestController implements ServiceRequestControllerDocs {

    private final ServiceRequestService serviceRequestService;

    @Override
    @GetMapping("/members/me/requests")
    public ResponseEntity<List<ServiceRequestListResponse>> getMyRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ServiceRequestListResponse> requests = serviceRequestService.getMyRequests(userDetails.getMemberId());
        return ResponseEntity.ok(requests);
    }

    @Override
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<ServiceRequestResponse> getRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long requestId
    ) {
        ServiceRequestResponse response = serviceRequestService.getRequest(userDetails.getMemberId(), requestId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/requests")
    public ResponseEntity<ServiceRequestCreateResponse> createRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ServiceRequestCreateRequest request
    ) {
        ServiceRequestCreateResponse response = serviceRequestService.createRequest(userDetails.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
