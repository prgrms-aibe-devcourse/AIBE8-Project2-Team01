package com.grepp.efy.domain.community.comment;

import com.grepp.efy.domain.community.comment.dto.PostCommentRequest;
import com.grepp.efy.domain.community.comment.dto.PostCommentResponse;
import com.grepp.efy.domain.community.post.Post;
import com.grepp.efy.domain.community.post.PostRepository;
import com.grepp.efy.domain.community.post.PostService;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.user.member.MemberRepository;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;
    private final SupabaseStorageClient storageClient;

    // 숨김 글의 댓글은 관리자만 조회 (그 외에는 글이 없는 것처럼 404)
    public PageResponse<PostCommentResponse> getComments(Long postId, int pageNumber, @Nullable MemberRole role) {

        Post post = findPost(postId);
        if (!post.isVisible() && role != MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
        }

        // 최신순 20개 고정, 같은 시각이면 commentId 내림차순
        Pageable pageable = PageRequest.of(pageNumber - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));

        Page<PostComment> page = postCommentRepository.findComments(postId, pageable);

        return PageResponse.of(page, page.getContent().stream()
                .map(this::toResponse)
                .toList());
    }

    // 삭제되지 않은 공개 글에만 작성 (숨김 글은 관리자도 404)
    @Transactional
    public PostCommentResponse createComment(Long postId, Long memberId, PostCommentRequest request) {

        Post post = findPost(postId);
        if (!post.isVisible()) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
        }

        List<Long> imageIds = Objects.requireNonNullElse(request.getImageIds(), List.of());
        List<UploadFile> images = postService.loadImages(memberId, imageIds);

        PostComment comment = postCommentRepository.save(PostComment.builder()
                .post(post)
                .member(memberRepository.findById(memberId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION)))
                .content(request.getContent())
                .images(images)
                .build());

        // 댓글 저장과 같은 트랜잭션에서 증가
        postRepository.increaseCommentCount(postId);

        return toResponse(comment);
    }

    // 작성자 본인만 (관리자 포함 타인은 403). 부모 글이 숨김이면 작성자도 404
    @Transactional
    public PostCommentResponse updateComment(Long commentId, Long memberId, PostCommentRequest request) {

        PostComment comment = findComment(commentId);

        if (!comment.getPost().isVisible()) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
        }
        if (!comment.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        // 전체 교체: imageIds를 안 보내면 이미지가 비워진다
        List<Long> imageIds = Objects.requireNonNullElse(request.getImageIds(), List.of());
        List<UploadFile> images = postService.loadImages(memberId, imageIds);

        comment.update(request.getContent(), images);

        return toResponse(comment);
    }

    // 작성자와 관리자만. 관리자는 숨김 글의 댓글도 삭제 가능
    @Transactional
    public void deleteComment(Long commentId, Long memberId, MemberRole role) {

        PostComment comment = findComment(commentId);

        if (role != MemberRole.ADMIN) {
            // 숨김 글의 댓글은 관리자만 (작성자도 404)
            if (!comment.getPost().isVisible()) {
                throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
            }
            if (!comment.getMember().getId().equals(memberId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
            }
        }

        comment.delete();

        // 댓글 삭제와 같은 트랜잭션에서 감소
        postRepository.decreaseCommentCount(comment.getPost().getId());
    }

    // 삭제된 댓글, 또는 부모 글이 삭제된 댓글은 없는 댓글과 동일하게 404
    private PostComment findComment(Long commentId) {
        return postCommentRepository.findById(commentId)
                .filter(comment -> comment.getDeletedAt() == null)
                .filter(comment -> comment.getPost().getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
    }

    // 삭제된 글은 없는 글과 동일하게 404
    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .filter(post -> post.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
    }

    private PostCommentResponse toResponse(PostComment comment) {
        List<PostResponse.PostImage> images = comment.getImages().stream()
                .map(file -> new PostResponse.PostImage(file.getId(), url(file)))
                .toList();

        UploadFile profile = comment.getMember().getImage();

        return PostCommentResponse.builder()
                .commentId(comment.getId())
                .memberId(comment.getMember().getId())
                .memberNickname(comment.getMember().getNickname())
                .memberProfileImageUrl(profile == null ? null : url(profile))
                .isExpert(comment.getMember().getRole() == MemberRole.EXPERT)
                .content(comment.getContent())
                .images(images)
                // 좋아요 도메인 구현 전까지 0 / false
                .likeCount(0)
                .liked(false)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private String url(UploadFile file) {
        return storageClient.createUrl(file.getVisibility(), file.getStoragePath());
    }
}
