package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.secondregion.docs.SecondRegionControllerDocs;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionCreateRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import com.grepp.efy.domain.basedata.region.RegionLock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SecondRegionController implements SecondRegionControllerDocs {

    private final RegionLock regionLock;
    private final SecondRegionService secondRegionService;

    @GetMapping("/public/first-regions/{firstRegionId}/second-regions")
    @Override
    public ResponseEntity<List<SecondRegionResponse>> getSecondRegions(@PathVariable Long firstRegionId) {
        return ResponseEntity.ok(secondRegionService.getSecondRegions(firstRegionId));
    }

    @PostMapping("/admin/first-regions/{firstRegionId}/second-regions")
    @Override
    public ResponseEntity<SecondRegionResponse> createSecondRegion(@PathVariable @Positive(message = "상위 항목 ID는 양수로 입력해 주세요.") Long firstRegionId, @Valid @RequestBody SecondRegionCreateRequest request) {
        synchronized (regionLock) {
            return ResponseEntity.status(HttpStatus.CREATED).body(secondRegionService.createSecondRegion(firstRegionId, request));
        }
    }

    @PutMapping("/admin/second-regions/{secondRegionId}")
    @Override
    public ResponseEntity<SecondRegionResponse> updateSecondRegion(@PathVariable Long secondRegionId, @Valid @RequestBody SecondRegionRequest request) {
        synchronized (regionLock) {
            return ResponseEntity.ok(secondRegionService.updateSecondRegion(secondRegionId, request));
        }
    }

    @DeleteMapping("/admin/second-regions/{secondRegionId}")
    @Override
    public ResponseEntity<SecondRegionResponse> deleteSecondRegion(@PathVariable Long secondRegionId) {
        synchronized (regionLock) {
            return ResponseEntity.ok(secondRegionService.deleteSecondRegion(secondRegionId));
        }
    }
}
