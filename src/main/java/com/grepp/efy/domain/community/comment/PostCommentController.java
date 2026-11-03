package com.grepp.efy.domain.community.comment;

import com.grepp.efy.domain.community.comment.docs.PostCommentControllerDocs;
import com.grepp.efy.domain.community.comment.dto.PostCommentRequest;
import com.grepp.efy.domain.community.comment.dto.PostCommentResponse;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostCommentController implements PostCommentControllerDocs {

    private final PostCommentService postCommentService;

    @Override
    @GetMapping("/public/posts/{postId}/comments")
    public ResponseEntity<PageResponse<PostCommentResponse>> getComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber
    ) {

        PageResponse<PostCommentResponse> response = postCommentService.getComments(
                postId, pageNumber, userDetails == null ? null : userDetails.getRole());

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<PostCommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId,
            @Valid @RequestBody PostCommentRequest request
    ) {

        PostCommentResponse response = postCommentService.createComment(postId, userDetails.getMemberId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
