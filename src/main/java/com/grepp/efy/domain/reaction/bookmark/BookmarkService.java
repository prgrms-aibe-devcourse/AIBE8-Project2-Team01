package com.grepp.efy.domain.reaction.bookmark;

import com.grepp.efy.domain.basedata.firstregion.FirstRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.community.post.PostMapper;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkCancelResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkSummaryResponse;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import com.grepp.efy.domain.user.expertprofile.ExpertProfileRepository;
import com.grepp.efy.domain.user.member.MemberRepository;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private static final int MAX_BOOKMARKS = 500;

    private final BookmarkRepository bookmarkRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final MemberRepository memberRepository;
    private final PostMapper postMapper;
    private final SupabaseStorageClient storageClient;

    // 활성 북마크만, 탈퇴한 고수 제외. 최신 북마크순(같으면 북마크 ID 내림차순) 20개 고정
    public PageResponse<BookmarkSummaryResponse> getMyBookmarks(Long memberId, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber - 1, 20,
                Sort.by(Sort.Direction.DESC, "bookmarkedAt").and(Sort.by(Sort.Direction.DESC, "id")));

        Page<Bookmark> page = bookmarkRepository.findActiveBookmarks(memberId, pageable);
        if (page.isEmpty()) {
            return PageResponse.of(page, List.of());
        }

        // 이 페이지 고수들의 활동 지역을 한 번에 모아 프로필 ID별로 묶는다 (ID 오름차순)
        List<Long> profileIds = page.getContent().stream().map(b -> b.getExpertProfile().getId()).toList();

        Map<Long, List<BookmarkSummaryResponse.WholeRegion>> wholeRegions = new HashMap<>();
        for (Object[] row : bookmarkRepository.findWholeRegions(profileIds)) {
            FirstRegion region = (FirstRegion) row[1];
            wholeRegions.computeIfAbsent((Long) row[0], id -> new ArrayList<>())
                    .add(new BookmarkSummaryResponse.WholeRegion(region.getId(), region.getName()));
        }
        wholeRegions.values().forEach(list -> list.sort(Comparator.comparing(BookmarkSummaryResponse.WholeRegion::firstRegionId)));

        Map<Long, List<PostResponse.PostRegion>> regions = new HashMap<>();
        for (Object[] row : bookmarkRepository.findRegions(profileIds)) {
            regions.computeIfAbsent((Long) row[0], id -> new ArrayList<>())
                    .add(postMapper.toRegion((SecondRegion) row[1]));
        }
        regions.values().forEach(list -> list.sort(Comparator.comparing(PostResponse.PostRegion::secondRegionId)));

        return PageResponse.of(page, page.getContent().stream()
                .map(bookmark -> {
                    ExpertProfile profile = bookmark.getExpertProfile();
                    UploadFile image = profile.getMember().getImage();
                    return BookmarkSummaryResponse.builder()
                            .expertProfileId(profile.getId())
                            .expertNickname(profile.getMember().getNickname())
                            .expertProfileImageUrl(image == null ? null
                                    : storageClient.createUrl(image.getVisibility(), image.getStoragePath()))
                            // ponytail: 리뷰 기능이 아직 없어 null / 0. 리뷰가 생기면 이 페이지 고수 ID로 한 번에 집계
                            .averageRating(null)
                            .reviewCount(0)
                            .category(postMapper.toCategory(profile.getPrimaryCategory()))
                            .nationwide(profile.isNationwide())
                            .wholeRegions(wholeRegions.getOrDefault(profile.getId(), List.of()))
                            .regions(regions.getOrDefault(profile.getId(), List.of()))
                            .bookmarkedAt(bookmark.getBookmarkedAt())
                            .build();
                })
                .toList());
    }

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
