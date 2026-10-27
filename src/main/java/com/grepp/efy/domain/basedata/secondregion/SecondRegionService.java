package com.grepp.efy.domain.basedata.secondregion;

import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionService;
import com.grepp.efy.domain.basedata.region.RegionCodeService;
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
    private final RegionCodeService regionCodeService;

    public List<SecondRegionResponse> getSecondRegions(Long firstRegionId) {
        firstRegionService.getFirstRegionEntity(firstRegionId);
        return secondRegionRepository.findByFirstRegionIdOrderByIdAsc(firstRegionId).stream()
                .map(secondRegionMapper::toResponse).toList();
    }

    @Transactional
    public SecondRegionResponse createSecondRegion(SecondRegionRequest request) {
        regionCodeService.lockForWrite();
        var firstRegion = firstRegionService.getFirstRegionEntity(request.getFirstRegionId());
        checkDuplicate(request, 0L);
        SecondRegion entity = secondRegionMapper.toEntity(request);
        entity.setFirstRegion(firstRegion);
        return secondRegionMapper.toResponse(secondRegionRepository.save(entity));
    }

    @Transactional
    public SecondRegionResponse updateSecondRegion(Long id, SecondRegionRequest request) {
        regionCodeService.lockForWrite();
        SecondRegion entity = getSecondRegionEntity(id);
        var firstRegion = firstRegionService.getFirstRegionEntity(request.getFirstRegionId());
        checkDuplicate(request, id);
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
        secondRegionRepository.flush();
        return response;
    }

    public SecondRegion getSecondRegionEntity(Long id) {
        return secondRegionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicate(SecondRegionRequest request, Long id) {
        if (secondRegionRepository.existsByNameIgnoreCaseAndFirstRegionIdAndIdNot(
                request.getName(), request.getFirstRegionId(), id)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REGION_EXCEPTION);
        }
        regionCodeService.checkDuplicate(request.getCode(), null, id);
    }
}
