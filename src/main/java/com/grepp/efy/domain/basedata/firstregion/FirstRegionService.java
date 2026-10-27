package com.grepp.efy.domain.basedata.firstregion;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
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
public class FirstRegionService {

    private final FirstRegionRepository firstRegionRepository;
    private final FirstRegionMapper firstRegionMapper;
    private final RegionCodeService regionCodeService;

    public List<FirstRegionResponse> getFirstRegions() {
        return firstRegionRepository.findAllByOrderByIdAsc().stream()
                .map(firstRegionMapper::toResponse).toList();
    }

    @Transactional
    public FirstRegionResponse createFirstRegion(FirstRegionRequest request) {
        regionCodeService.lockForWrite();
        checkDuplicate(request, 0L);
        FirstRegion entity = firstRegionMapper.toEntity(request);
        return firstRegionMapper.toResponse(firstRegionRepository.save(entity));
    }

    @Transactional
    public FirstRegionResponse updateFirstRegion(Long id, FirstRegionRequest request) {
        regionCodeService.lockForWrite();
        FirstRegion entity = getFirstRegionEntity(id);
        checkDuplicate(request, id);
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
        firstRegionRepository.flush();
        return response;
    }

    public FirstRegion getFirstRegionEntity(Long id) {
        return firstRegionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));
    }

    private void checkDuplicate(FirstRegionRequest request, Long id) {
        if (firstRegionRepository.existsByNameIgnoreCaseAndIdNot(
                request.getName(), id)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REGION_EXCEPTION);
        }
        regionCodeService.checkDuplicate(request.getCode(), id, null);
    }
}
