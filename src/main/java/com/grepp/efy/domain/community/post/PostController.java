package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.community.post.docs.PostControllerDocs;
import com.grepp.efy.domain.community.post.dto.PostRequest;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController implements PostControllerDocs {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostRequest postRequest) {

        PostResponse response = postService.createPost(userDetails.getMemberId(), postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
