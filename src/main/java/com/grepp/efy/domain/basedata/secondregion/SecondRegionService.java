package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionCreateRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionService;
import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecondRegionService {

    private final SecondRegionRepository secondRegionRepository;
    private final SecondRegionMapper secondRegionMapper;
    private final FirstRegionService firstRegionService;
    private final FirstRegionRepository firstRegionRepository;

    public List<SecondRegionResponse> getSecondRegions(Long firstRegionId) {
        firstRegionService.getFirstRegionEntity(firstRegionId);
        List<SecondRegion> secondRegionList = secondRegionRepository.findByFirstRegionIdOrderByIdAsc(firstRegionId);
        return secondRegionMapper.toResponseList(secondRegionList);
    }

    @Transactional
    public SecondRegionResponse createSecondRegion(Long firstRegionId, SecondRegionCreateRequest request) {
        FirstRegion firstRegion = firstRegionService.getFirstRegionEntity(firstRegionId);
        checkDuplicateName(request.getName(), firstRegionId);
        checkDuplicateCode(request.getCode());
        SecondRegion entity = secondRegionMapper.toEntity(request);
        entity.setFirstRegion(firstRegion);
        SecondRegion savedSecondRegion = secondRegionRepository.save(entity);
        return secondRegionMapper.toResponse(savedSecondRegion);
    }

    @Transactional
    public SecondRegionResponse updateSecondRegion(Long id, SecondRegionRequest request) {
        SecondRegion entity = getSecondRegionEntity(id);
        FirstRegion firstRegion = firstRegionService.getFirstRegionEntity(request.getFirstRegionId());
        boolean nameChanged = !entity.getName().equalsIgnoreCase(request.getName());
        boolean parentChanged = !entity.getFirstRegion().getId().equals(request.getFirstRegionId());
        if (nameChanged || parentChanged) {
            checkDuplicateName(request.getName(), request.getFirstRegionId());
        }
        boolean codeChanged = !entity.getCode().equals(request.getCode());
        if (codeChanged) {
            checkDuplicateCode(request.getCode());
        }
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setFirstRegion(firstRegion);
        return secondRegionMapper.toResponse(entity);
    }

    @Transactional
    public SecondRegionResponse deleteSecondRegion(Long id) {
        SecondRegion entity = getSecondRegionEntity(id);
        SecondRegionResponse response = secondRegionMapper.toResponse(entity);
        // FK로 참조 중이면 DB가 삭제를 거절하고 공통 핸들러가 409로 응답한다.
        secondRegionRepository.delete(entity);
        return response;
    }

    public SecondRegion getSecondRegionEntity(Long id) {
        return secondRegionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicateName(String name, Long firstRegionId) {
        if (secondRegionRepository.existsByNameIgnoreCaseAndFirstRegionId(
                name, firstRegionId)) {
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
