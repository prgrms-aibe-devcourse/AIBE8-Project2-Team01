package com.grepp.efy.domain.basedata.firstcategory;

import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryRequest;
import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FirstCategoryService {

    private final FirstCategoryRepository firstCategoryRepository;
    private final FirstCategoryMapper firstCategoryMapper;

    public List<FirstCategoryResponse> getFirstCategories() {
        return firstCategoryRepository.findAllByOrderByIdAsc().stream()
                .map(firstCategoryMapper::toResponse).toList();
    }

    @Transactional
    public FirstCategoryResponse createFirstCategory(FirstCategoryRequest request) {
        checkDuplicate(request, 0L);
        FirstCategory entity = firstCategoryMapper.toEntity(request);
        return firstCategoryMapper.toResponse(firstCategoryRepository.save(entity));
    }

    @Transactional
    public FirstCategoryResponse updateFirstCategory(Long id, FirstCategoryRequest request) {
        FirstCategory entity = getFirstCategoryEntity(id);
        checkDuplicate(request, id);
        entity.setName(request.getName());
        return firstCategoryMapper.toResponse(entity);
    }

    @Transactional
    public FirstCategoryResponse deleteFirstCategory(Long id) {
        FirstCategory entity = getFirstCategoryEntity(id);
        FirstCategoryResponse response = firstCategoryMapper.toResponse(entity);
        // FK로 참조 중이면 DB가 삭제를 거절하고 공통 핸들러가 409로 응답한다.
        firstCategoryRepository.delete(entity);
        firstCategoryRepository.flush();
        return response;
    }

    public FirstCategory getFirstCategoryEntity(Long id) {
        return firstCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicate(FirstCategoryRequest request, Long id) {
        if (firstCategoryRepository.existsByNameIgnoreCaseAndIdNot(
                request.getName(), id)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_EXCEPTION);
        }
    }
}
