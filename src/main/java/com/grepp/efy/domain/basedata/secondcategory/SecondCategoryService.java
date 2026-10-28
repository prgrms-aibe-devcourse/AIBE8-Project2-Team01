package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryCreateRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
import com.grepp.efy.domain.basedata.firstcategory.FirstCategoryService;
import com.grepp.efy.domain.basedata.firstcategory.FirstCategory;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecondCategoryService {

    private final SecondCategoryRepository secondCategoryRepository;
    private final SecondCategoryMapper secondCategoryMapper;
    private final FirstCategoryService firstCategoryService;

    public List<SecondCategoryResponse> getSecondCategories(Long firstCategoryId) {
        firstCategoryService.getFirstCategoryEntity(firstCategoryId);
        List<SecondCategory> secondCategoryList = secondCategoryRepository.findByFirstCategoryIdOrderByIdAsc(firstCategoryId);
        return secondCategoryMapper.toResponseList(secondCategoryList);
    }

    @Transactional
    public SecondCategoryResponse createSecondCategory(Long firstCategoryId, SecondCategoryCreateRequest request) {
        FirstCategory firstCategory = firstCategoryService.getFirstCategoryEntity(firstCategoryId);
        checkDuplicateName(request.getName(), firstCategoryId);
        SecondCategory entity = secondCategoryMapper.toEntity(request);
        entity.setFirstCategory(firstCategory);
        SecondCategory savedSecondCategory = secondCategoryRepository.save(entity);
        return secondCategoryMapper.toResponse(savedSecondCategory);
    }

    @Transactional
    public SecondCategoryResponse updateSecondCategory(Long id, SecondCategoryRequest request) {
        SecondCategory entity = getSecondCategoryEntity(id);
        FirstCategory firstCategory = firstCategoryService.getFirstCategoryEntity(request.getFirstCategoryId());
        boolean nameChanged = !entity.getName().equalsIgnoreCase(request.getName());
        boolean parentChanged = !entity.getFirstCategory().getId().equals(request.getFirstCategoryId());
        if (nameChanged || parentChanged) {
            checkDuplicateName(request.getName(), request.getFirstCategoryId());
        }
        entity.setName(request.getName());
        entity.setFirstCategory(firstCategory);
        return secondCategoryMapper.toResponse(entity);
    }

    @Transactional
    public SecondCategoryResponse deleteSecondCategory(Long id) {
        SecondCategory entity = getSecondCategoryEntity(id);
        SecondCategoryResponse response = secondCategoryMapper.toResponse(entity);
        // FK로 참조 중이면 DB가 삭제를 거절하고 공통 핸들러가 409로 응답한다.
        secondCategoryRepository.delete(entity);
        return response;
    }

    public SecondCategory getSecondCategoryEntity(Long id) {
        return secondCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicateName(String name, Long firstCategoryId) {
        if (secondCategoryRepository.existsByNameIgnoreCaseAndFirstCategoryId(
                name, firstCategoryId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_EXCEPTION);
        }
    }
}
