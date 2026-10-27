package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.secondcategory.docs.SecondCategoryControllerDocs;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SecondCategoryController implements SecondCategoryControllerDocs {

    private final SecondCategoryService secondCategoryService;

    @GetMapping("/public/first-categories/{firstCategoryId}/second-categories")
    @Override
    public ResponseEntity<List<SecondCategoryResponse>> getSecondCategories(@PathVariable Long firstCategoryId) {
        return ResponseEntity.ok(secondCategoryService.getSecondCategories(firstCategoryId));
    }

    @PostMapping("/admin/second-categories")
    @Override
    public ResponseEntity<SecondCategoryResponse> createSecondCategory(@Valid @RequestBody SecondCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secondCategoryService.createSecondCategory(request));
    }

    @PutMapping("/admin/second-categories/{id}")
    @Override
    public ResponseEntity<SecondCategoryResponse> updateSecondCategory(@PathVariable Long id, @Valid @RequestBody SecondCategoryRequest request) {
        return ResponseEntity.ok(secondCategoryService.updateSecondCategory(id, request));
    }

    @DeleteMapping("/admin/second-categories/{id}")
    @Override
    public ResponseEntity<SecondCategoryResponse> deleteSecondCategory(@PathVariable Long id) {
        return ResponseEntity.ok(secondCategoryService.deleteSecondCategory(id));
    }
}
