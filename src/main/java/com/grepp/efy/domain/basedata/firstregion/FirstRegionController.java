package com.grepp.efy.domain.basedata.firstregion;

import com.grepp.efy.domain.basedata.firstregion.docs.FirstRegionControllerDocs;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FirstRegionController implements FirstRegionControllerDocs {

    private final FirstRegionService firstRegionService;

    @GetMapping("/public/first-regions")
    @Override
    public ResponseEntity<List<FirstRegionResponse>> getFirstRegions() {
        return ResponseEntity.ok(firstRegionService.getFirstRegions());
    }

    @PostMapping("/admin/first-regions")
    @Override
    public ResponseEntity<FirstRegionResponse> createFirstRegion(@Valid @RequestBody FirstRegionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(firstRegionService.createFirstRegion(request));
    }

    @PutMapping("/admin/first-regions/{id}")
    @Override
    public ResponseEntity<FirstRegionResponse> updateFirstRegion(@PathVariable Long id, @Valid @RequestBody FirstRegionRequest request) {
        return ResponseEntity.ok(firstRegionService.updateFirstRegion(id, request));
    }

    @DeleteMapping("/admin/first-regions/{id}")
    @Override
    public ResponseEntity<FirstRegionResponse> deleteFirstRegion(@PathVariable Long id) {
        return ResponseEntity.ok(firstRegionService.deleteFirstRegion(id));
    }
}
