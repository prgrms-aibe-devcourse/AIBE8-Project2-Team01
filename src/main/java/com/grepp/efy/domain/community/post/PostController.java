package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.community.post.docs.PostControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController implements PostControllerDocs {

    private final PostService postService;
}
