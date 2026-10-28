package com.grepp.efy.domain.basedata.firstregion;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FirstRegionService {

    private final FirstRegionRepository firstRegionRepository;
    private final FirstRegionMapper firstRegionMapper;
    private final SecondRegionRepository secondRegionRepository;

    public List<FirstRegionResponse> getFirstRegions() {
        List<FirstRegion> firstRegionList = firstRegionRepository.findAllByOrderByIdAsc();
        return firstRegionMapper.toResponseList(firstRegionList);
    }

    @Transactional
    public FirstRegionResponse createFirstRegion(FirstRegionRequest request) {
        checkDuplicateName(request);
        checkDuplicateCode(request.getCode());
        FirstRegion entity = firstRegionMapper.toEntity(request);
        FirstRegion savedFirstRegion = firstRegionRepository.save(entity);
        return firstRegionMapper.toResponse(savedFirstRegion);
    }

    @Transactional
    public FirstRegionResponse updateFirstRegion(Long id, FirstRegionRequest request) {
        FirstRegion entity = getFirstRegionEntity(id);
        boolean nameChanged = !entity.getName().equalsIgnoreCase(request.getName());
        if (nameChanged) {
            checkDuplicateName(request);
        }
        boolean codeChanged = !entity.getCode().equals(request.getCode());
        if (codeChanged) {
            checkDuplicateCode(request.getCode());
        }
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        return firstRegionMapper.toResponse(entity);
    }

    @Transactional
    public FirstRegionResponse deleteFirstRegion(Long id) {
        FirstRegion entity = getFirstRegionEntity(id);
        FirstRegionResponse response = firstRegionMapper.toResponse(entity);
        // FK로 참조 중이면 DB가 삭제를 거절하고 공통 핸들러가 409로 응답한다.
        firstRegionRepository.delete(entity);
        return response;
    }

    public FirstRegion getFirstRegionEntity(Long id) {
        return firstRegionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicateName(FirstRegionRequest request) {
        if (firstRegionRepository.existsByNameIgnoreCase(
                request.getName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_REGION_EXCEPTION);
        }
    }

    private void checkDuplicateCode(String code) {
        boolean duplicate = firstRegionRepository.existsByCode(code)
                || secondRegionRepository.existsByCode(code);
        if (duplicate) {
            throw new BusinessException(ErrorCode.DUPLICATE_REGION_CODE_EXCEPTION);
        }
    }
}
