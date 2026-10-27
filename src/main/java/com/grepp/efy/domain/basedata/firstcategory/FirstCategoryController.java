package com.grepp.efy.domain.basedata.firstcategory;

import com.grepp.efy.domain.basedata.firstcategory.docs.FirstCategoryControllerDocs;
import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryRequest;
import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FirstCategoryController implements FirstCategoryControllerDocs {

    private final FirstCategoryService firstCategoryService;

    @GetMapping("/public/first-categories")
    @Override
    public ResponseEntity<List<FirstCategoryResponse>> getFirstCategories() {
        return ResponseEntity.ok(firstCategoryService.getFirstCategories());
    }

    @PostMapping("/admin/first-categories")
    @Override
    public ResponseEntity<FirstCategoryResponse> createFirstCategory(@Valid @RequestBody FirstCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(firstCategoryService.createFirstCategory(request));
    }

    @PutMapping("/admin/first-categories/{id}")
    @Override
    public ResponseEntity<FirstCategoryResponse> updateFirstCategory(@PathVariable Long id, @Valid @RequestBody FirstCategoryRequest request) {
        return ResponseEntity.ok(firstCategoryService.updateFirstCategory(id, request));
    }

    @DeleteMapping("/admin/first-categories/{id}")
    @Override
    public ResponseEntity<FirstCategoryResponse> deleteFirstCategory(@PathVariable Long id) {
        return ResponseEntity.ok(firstCategoryService.deleteFirstCategory(id));
    }
}
