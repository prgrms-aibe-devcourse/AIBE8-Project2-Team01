package com.grepp.efy.domain.reaction.bookmark;

import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkCancelResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkResponse;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import com.grepp.efy.domain.user.expertprofile.ExpertProfileRepository;
import com.grepp.efy.domain.user.member.MemberRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private static final int MAX_BOOKMARKS = 500;

    private final BookmarkRepository bookmarkRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final MemberRepository memberRepository;

    // 판단 순서: 고수 프로필 없음·탈퇴(404) → 이미 북마크(409) → 개수 초과(409)
    @Transactional
    public BookmarkResponse createBookmark(Long memberId, Long expertProfileId) {

        ExpertProfile expertProfile = findExpertProfile(expertProfileId);

        Optional<Bookmark> found = bookmarkRepository.findByMemberIdAndExpertProfileId(memberId, expertProfileId);
        if (found.isPresent() && found.get().isActive()) {
            throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED_EXCEPTION);
        }

        // ponytail: 동시에 여러 고수를 등록하면 500개를 조금 넘을 수 있음. 엄격히 막아야 하면 회원 행에 락
        if (bookmarkRepository.countActive(memberId) >= MAX_BOOKMARKS) {
            throw new BusinessException(ErrorCode.BOOKMARK_LIMIT_EXCEEDED_EXCEPTION);
        }

        // 취소한 기록이 있으면 새로 만들지 않고 되살린다
        if (found.isPresent()) {
            Bookmark bookmark = found.get();
            bookmark.activate();
            return toResponse(bookmark);
        }

        // 위 확인을 동시에 통과한 요청은 유니크 제약에 걸린다 → 같은 409로 변환
        try {
            Bookmark bookmark = bookmarkRepository.saveAndFlush(
                    new Bookmark(memberRepository.getReferenceById(memberId), expertProfile));
            return toResponse(bookmark);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED_EXCEPTION);
        }
    }

    // 기록은 남기고 비활성으로. 판단 순서: 고수 프로필 없음·탈퇴(404) → 활성 북마크 없음(404)
    @Transactional
    public BookmarkCancelResponse cancelBookmark(Long memberId, Long expertProfileId) {

        findExpertProfile(expertProfileId);

        if (bookmarkRepository.deactivate(memberId, expertProfileId) == 0) {
            throw new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND_EXCEPTION);
        }

        return new BookmarkCancelResponse(expertProfileId, false);
    }

    // 고수 프로필은 회원과 1:1이고 따로 삭제 표시가 없으므로, 회원이 탈퇴했으면 없는 프로필로 취급
    private ExpertProfile findExpertProfile(Long expertProfileId) {
        return expertProfileRepository.findById(expertProfileId)
                .filter(profile -> profile.getMember().getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPERT_PROFILE_NOT_FOUND_EXCEPTION));
    }

    private static BookmarkResponse toResponse(Bookmark bookmark) {
        return new BookmarkResponse(bookmark.getExpertProfile().getId(), bookmark.isActive(), bookmark.getBookmarkedAt());
    }
}
