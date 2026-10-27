package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.secondregion.docs.SecondRegionControllerDocs;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SecondRegionController implements SecondRegionControllerDocs {

    private final SecondRegionService secondRegionService;

    @GetMapping("/public/first-regions/{firstRegionId}/second-regions")
    @Override
    public ResponseEntity<List<SecondRegionResponse>> getSecondRegions(@PathVariable Long firstRegionId) {
        return ResponseEntity.ok(secondRegionService.getSecondRegions(firstRegionId));
    }

    @PostMapping("/admin/second-regions")
    @Override
    public ResponseEntity<SecondRegionResponse> createSecondRegion(@Valid @RequestBody SecondRegionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secondRegionService.createSecondRegion(request));
    }

    @PutMapping("/admin/second-regions/{id}")
    @Override
    public ResponseEntity<SecondRegionResponse> updateSecondRegion(@PathVariable Long id, @Valid @RequestBody SecondRegionRequest request) {
        return ResponseEntity.ok(secondRegionService.updateSecondRegion(id, request));
    }

    @DeleteMapping("/admin/second-regions/{id}")
    @Override
    public ResponseEntity<SecondRegionResponse> deleteSecondRegion(@PathVariable Long id) {
        return ResponseEntity.ok(secondRegionService.deleteSecondRegion(id));
    }
}
