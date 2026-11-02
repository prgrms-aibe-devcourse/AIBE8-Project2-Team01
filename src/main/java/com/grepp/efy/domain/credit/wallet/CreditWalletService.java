package com.grepp.efy.domain.credit.wallet;

import com.grepp.efy.domain.credit.wallet.dto.CreditBalanceResponse;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditWalletService {
    private static final long CREDIT_PER_QUOTE = 2;
    private final CreditWalletRepository creditWalletRepository;
    private final MemberService memberService;

    public CreditBalanceResponse getMyCredit(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);

        return null;
    }
}
