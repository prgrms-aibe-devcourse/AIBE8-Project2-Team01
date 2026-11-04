package com.grepp.efy.domain.credit.history;

import com.grepp.efy.domain.credit.CreditAccessValidator;
import com.grepp.efy.domain.credit.history.dto.CreditHistoryResponse;
import com.grepp.efy.domain.credit.wallet.CreditWallet;
import com.grepp.efy.domain.credit.wallet.CreditWalletRepository;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditHistoryService {

    private static final int MAX_PAGE_SIZE = 50;

    private final CreditHistoryRepository creditHistoryRepository;
    private final CreditWalletRepository creditWalletRepository;
    private final CreditAccessValidator creditAccessValidator;

    // 내 크레딧 내역을 최신순으로 조회한다. type이 없으면 전체, 있으면 해당 유형만 조회한다
    public PageResponse<CreditHistoryResponse> getMyHistories(Long memberId, CreditHistoryType type,
                                                              int pageNumber, int pageSize) {
        creditAccessValidator.getExpert(memberId);
        if (pageNumber < 1 || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_PAGE_REQUEST_EXCEPTION);
        }
        // 같은 시각에 생긴 내역도 순서가 고정되도록 id를 두 번째 정렬 기준으로 쓴다
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        // 아직 충전한 적이 없어 지갑이 없으면 빈 페이지를 반환한다
        CreditWallet wallet = creditWalletRepository.findByMemberId(memberId).orElse(null);
        if (wallet == null) {
            return PageResponse.of(Page.empty(pageable), List.of());
        }

        Page<CreditHistory> page = (type == null)
                ? creditHistoryRepository.findByCreditWalletId(wallet.getId(), pageable)
                : creditHistoryRepository.findByCreditWalletIdAndHistoryType(wallet.getId(), type, pageable);
        List<CreditHistoryResponse> content = page.getContent().stream()
                .map(CreditHistoryResponse::from)
                .toList();
        return PageResponse.of(page, content);
    }
}