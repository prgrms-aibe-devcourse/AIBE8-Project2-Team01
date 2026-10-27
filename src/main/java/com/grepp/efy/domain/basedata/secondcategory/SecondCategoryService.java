package com.grepp.efy.domain.basedata.secondcategory;

import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryResponse;
import com.grepp.efy.domain.basedata.firstcategory.FirstCategoryService;
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
        return secondCategoryRepository.findByFirstCategoryIdOrderByIdAsc(firstCategoryId).stream()
                .map(secondCategoryMapper::toResponse).toList();
    }

    @Transactional
    public SecondCategoryResponse createSecondCategory(SecondCategoryRequest request) {
        var firstCategory = firstCategoryService.getFirstCategoryEntity(request.getFirstCategoryId());
        checkDuplicate(request, 0L);
        SecondCategory entity = secondCategoryMapper.toEntity(request);
        entity.setFirstCategory(firstCategory);
        return secondCategoryMapper.toResponse(secondCategoryRepository.save(entity));
    }

    @Transactional
    public SecondCategoryResponse updateSecondCategory(Long id, SecondCategoryRequest request) {
        SecondCategory entity = getSecondCategoryEntity(id);
        var firstCategory = firstCategoryService.getFirstCategoryEntity(request.getFirstCategoryId());
        checkDuplicate(request, id);
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
        secondCategoryRepository.flush();
        return response;
    }

    public SecondCategory getSecondCategoryEntity(Long id) {
        return secondCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicate(SecondCategoryRequest request, Long id) {
        if (secondCategoryRepository.existsByNameIgnoreCaseAndFirstCategoryIdAndIdNot(
                request.getName(), request.getFirstCategoryId(), id)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_EXCEPTION);
        }
    }
}
