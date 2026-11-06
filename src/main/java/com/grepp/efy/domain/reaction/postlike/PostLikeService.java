package com.grepp.efy.domain.reaction.postlike;

import com.grepp.efy.domain.community.comment.PostComment;
import com.grepp.efy.domain.community.comment.PostCommentRepository;
import com.grepp.efy.domain.community.comment.PostCommentService;
import com.grepp.efy.domain.community.post.Post;
import com.grepp.efy.domain.community.post.PostRepository;
import com.grepp.efy.domain.community.post.PostService;
import com.grepp.efy.domain.reaction.postlike.dto.PostCommentLikeResponse;
import com.grepp.efy.domain.reaction.postlike.dto.PostLikeResponse;
import com.grepp.efy.domain.user.member.MemberRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;
    private final PostCommentService postCommentService;

    // ---------- 게시글 ----------

    public PostLikeResponse likePost(Long postId, Long memberId) {

        Post post = findVisiblePost(postId);

        if (postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED_POST_EXCEPTION);
        }

        // 위 확인을 동시에 통과한 요청은 유니크 제약에 걸린다 → 같은 409로 변환
        try {
            postLikeRepository.saveAndFlush(new PostLike(memberRepository.getReferenceById(memberId), post));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED_POST_EXCEPTION);
        }

        postRepository.increaseLikeCount(postId);

        return new PostLikeResponse(postId, true, postRepository.findLikeCount(postId));
    }

    public PostLikeResponse unlikePost(Long postId, Long memberId) {

        findVisiblePost(postId);

        // 지운 행이 없으면 좋아요한 적이 없거나 다른 요청이 먼저 취소한 것
        if (postLikeRepository.deleteByMemberIdAndPostId(memberId, postId) == 0) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND_EXCEPTION);
        }

        postRepository.decreaseLikeCount(postId);

        return new PostLikeResponse(postId, false, postRepository.findLikeCount(postId));
    }

    // ---------- 댓글 ----------

    public PostCommentLikeResponse likeComment(Long commentId, Long memberId) {

        PostComment comment = findVisibleComment(commentId);

        if (postCommentLikeRepository.existsByMemberIdAndCommentId(memberId, commentId)) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED_COMMENT_EXCEPTION);
        }

        try {
            postCommentLikeRepository.saveAndFlush(new PostCommentLike(memberRepository.getReferenceById(memberId), comment));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED_COMMENT_EXCEPTION);
        }

        postCommentRepository.increaseLikeCount(commentId);

        return new PostCommentLikeResponse(commentId, true, postCommentRepository.findLikeCount(commentId));
    }

    public PostCommentLikeResponse unlikeComment(Long commentId, Long memberId) {

        findVisibleComment(commentId);

        if (postCommentLikeRepository.deleteByMemberIdAndCommentId(memberId, commentId) == 0) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND_EXCEPTION);
        }

        postCommentRepository.decreaseLikeCount(commentId);

        return new PostCommentLikeResponse(commentId, false, postCommentRepository.findLikeCount(commentId));
    }

    // 삭제·숨김 글은 관리자를 포함해 404
    private Post findVisiblePost(Long postId) {
        Post post = postService.findPost(postId);
        if (!post.isVisible()) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
        }
        return post;
    }

    // 삭제된 댓글, 부모 글이 삭제·숨김인 댓글은 관리자를 포함해 404
    private PostComment findVisibleComment(Long commentId) {
        PostComment comment = postCommentService.findComment(commentId);
        if (!comment.getPost().isVisible()) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
        }
        return comment;
    }
}
