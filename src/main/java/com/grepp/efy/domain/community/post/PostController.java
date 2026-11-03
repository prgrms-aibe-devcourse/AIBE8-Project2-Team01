package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.community.post.docs.PostControllerDocs;
import com.grepp.efy.domain.community.post.dto.PostRequest;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.community.post.dto.PostSearchCondition;
import com.grepp.efy.domain.community.post.dto.PostSummaryResponse;
import com.grepp.efy.domain.community.post.dto.PostUpdateRequest;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @GetMapping("/public/posts")
    public ResponseEntity<PageResponse<PostSummaryResponse>> getPublicPosts(
            @RequestParam BoardType boardType,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber,
            @RequestParam(required = false) @Size(min = 1, max = 50) @Pattern(regexp = ".*\\S.*") String keyword,
            @RequestParam(required = false) Long firstCategoryId,
            @RequestParam(required = false) Long secondCategoryId,
            @RequestParam(required = false) Long firstRegionId,
            @RequestParam(required = false) Long secondRegionId
    ) {

        PostSearchCondition condition = PostSearchCondition.builder()
                .boardType(boardType)
                .pageNumber(pageNumber)
                .keyword(keyword)
                .firstCategoryId(firstCategoryId)
                .secondCategoryId(secondCategoryId)
                .firstRegionId(firstRegionId)
                .secondRegionId(secondRegionId)
                .build();

        PageResponse<PostSummaryResponse> response = postService.getPublicPosts(condition);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/members/me/posts")
    public ResponseEntity<PageResponse<PostSummaryResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) BoardType boardType,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber
    ) {

        PostSearchCondition condition = PostSearchCondition.builder()
                .boardType(boardType)
                .pageNumber(pageNumber)
                .build();

        PageResponse<PostSummaryResponse> response = postService.getMyPosts(userDetails.getMemberId(), condition);

        return ResponseEntity.ok(response);
    }

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
