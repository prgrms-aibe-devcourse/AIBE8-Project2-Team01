package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.community.post.docs.PostControllerDocs;
import com.grepp.efy.domain.community.post.dto.PostRequest;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.community.post.dto.PostUpdateRequest;
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
public class PostController implements PostControllerDocs {

    private final PostService postService;

    @Override
    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId
    ) {

        PostResponse response = postService.getPostById(postId, userDetails == null ? null : userDetails.getRole());

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostRequest postRequest) {

        PostResponse response = postService.createPost(userDetails.getMemberId(), postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {

        PostResponse response = postService.updatePost(postId ,userDetails.getMemberId(), postUpdateRequest);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long postId
    ) {

        postService.deletePost(postId, userDetails.getMemberId(), userDetails.getRole());

        return ResponseEntity.noContent().build();
    }
}
