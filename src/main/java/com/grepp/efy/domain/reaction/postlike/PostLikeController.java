package com.grepp.efy.domain.reaction.postlike;

import com.grepp.efy.domain.reaction.postlike.docs.PostLikeControllerDocs;
import com.grepp.efy.domain.reaction.postlike.dto.PostCommentLikeResponse;
import com.grepp.efy.domain.reaction.postlike.dto.PostLikeResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostLikeController implements PostLikeControllerDocs {

    private final PostLikeService postLikeService;

    @Override
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<PostLikeResponse> likePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId
    ) {

        PostLikeResponse response = postLikeService.likePost(postId, userDetails.getMemberId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 다른 삭제 API와 달리 취소 후 likeCount를 담아 200으로 응답
    @Override
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<PostLikeResponse> unlikePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId
    ) {

        PostLikeResponse response = postLikeService.unlikePost(postId, userDetails.getMemberId());

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<PostCommentLikeResponse> likeComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long commentId
    ) {

        PostCommentLikeResponse response = postLikeService.likeComment(commentId, userDetails.getMemberId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @DeleteMapping("/comments/{commentId}/likes")
    public ResponseEntity<PostCommentLikeResponse> unlikeComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long commentId
    ) {

        PostCommentLikeResponse response = postLikeService.unlikeComment(commentId, userDetails.getMemberId());

        return ResponseEntity.ok(response);
    }
}
