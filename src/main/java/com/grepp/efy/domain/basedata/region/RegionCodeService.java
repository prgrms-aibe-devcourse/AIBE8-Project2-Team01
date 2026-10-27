package com.grepp.efy.domain.basedata.region;

import com.grepp.efy.domain.basedata.firstregion.FirstRegionRepository;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionCodeService {

    private final FirstRegionRepository firstRegionRepository;
    private final SecondRegionRepository secondRegionRepository;
    // shortcut: 단일 서버의 동시 등록을 직렬화한다. 서버를 여러 개로 늘리면 DB 공통 코드 제약으로 옮긴다.
    private final ReentrantLock codeLock = new ReentrantLock();

    public void lockForWrite() {
        codeLock.lock();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                codeLock.unlock();
            }
        });
    }

    public void checkDuplicate(String code, Long firstRegionId, Long secondRegionId) {
        if (firstRegionRepository.existsByCodeAndIdNot(code, firstRegionId == null ? 0L : firstRegionId)
                || secondRegionRepository.existsByCodeAndIdNot(code, secondRegionId == null ? 0L : secondRegionId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REGION_CODE_EXCEPTION);
        }
    }
}
