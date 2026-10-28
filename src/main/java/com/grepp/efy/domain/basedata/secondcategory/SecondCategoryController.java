package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.secondcategory.docs.SecondCategoryControllerDocs;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryCreateRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
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
public class SecondCategoryController implements SecondCategoryControllerDocs {

    private final SecondCategoryService secondCategoryService;

    @GetMapping("/public/first-categories/{firstCategoryId}/second-categories")
    @Override
    public ResponseEntity<List<SecondCategoryResponse>> getSecondCategories(@PathVariable Long firstCategoryId) {
        return ResponseEntity.ok(secondCategoryService.getSecondCategories(firstCategoryId));
    }

    @PostMapping("/admin/first-categories/{firstCategoryId}/second-categories")
    @Override
    public ResponseEntity<SecondCategoryResponse> createSecondCategory(@PathVariable @Positive(message = "상위 항목 ID는 양수로 입력해 주세요.") Long firstCategoryId, @Valid @RequestBody SecondCategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secondCategoryService.createSecondCategory(firstCategoryId, request));
    }

    @PutMapping("/admin/second-categories/{secondCategoryId}")
    @Override
    public ResponseEntity<SecondCategoryResponse> updateSecondCategory(@PathVariable Long secondCategoryId, @Valid @RequestBody SecondCategoryRequest request) {
        return ResponseEntity.ok(secondCategoryService.updateSecondCategory(secondCategoryId, request));
    }

    @DeleteMapping("/admin/second-categories/{secondCategoryId}")
    @Override
    public ResponseEntity<SecondCategoryResponse> deleteSecondCategory(@PathVariable Long secondCategoryId) {
        return ResponseEntity.ok(secondCategoryService.deleteSecondCategory(secondCategoryId));
    }
}
