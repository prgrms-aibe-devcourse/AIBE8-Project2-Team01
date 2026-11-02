package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.community.post.docs.PostControllerDocs;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController implements PostControllerDocs {

    private final PostService postService;
}
