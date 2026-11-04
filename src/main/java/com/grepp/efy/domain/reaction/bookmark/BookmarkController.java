package com.grepp.efy.domain.reaction.bookmark;

import com.grepp.efy.domain.reaction.bookmark.docs.BookmarkControllerDocs;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkResponse;
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
    @PostMapping("/{expertProfileId}")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long expertProfileId
    ) {

        BookmarkResponse response = bookmarkService.createBookmark(userDetails.getMemberId(), expertProfileId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
