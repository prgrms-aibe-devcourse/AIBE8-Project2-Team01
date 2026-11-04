package com.grepp.efy.domain.reaction.bookmark;

import com.grepp.efy.domain.reaction.bookmark.docs.BookmarkControllerDocs;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkCancelResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkSummaryResponse;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/me/bookmarks")
public class BookmarkController implements BookmarkControllerDocs {

    private final BookmarkService bookmarkService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<BookmarkSummaryResponse>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber
    ) {

        PageResponse<BookmarkSummaryResponse> response = bookmarkService.getMyBookmarks(userDetails.getMemberId(), pageNumber);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{expertProfileId}")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long expertProfileId
    ) {

        BookmarkResponse response = bookmarkService.createBookmark(userDetails.getMemberId(), expertProfileId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 기록은 남기고 비활성으로 바꾸므로, 결과를 담아 200으로 응답
    @Override
    @DeleteMapping("/{expertProfileId}")
    public ResponseEntity<BookmarkCancelResponse> cancelBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long expertProfileId
    ) {

        BookmarkCancelResponse response = bookmarkService.cancelBookmark(userDetails.getMemberId(), expertProfileId);

        return ResponseEntity.ok(response);
    }
}
