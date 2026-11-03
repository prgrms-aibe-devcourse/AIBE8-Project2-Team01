package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.basedata.firstcategory.FirstCategoryRepository;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionRepository;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryRepository;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionRepository;
import com.grepp.efy.domain.community.post.dto.*;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.file.UploadFileRepository;
import com.grepp.efy.domain.file.UploadFileVisibility;
import com.grepp.efy.domain.user.member.Member;
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FirstCategoryRepository firstCategoryRepository;
    private final FirstRegionRepository firstRegionRepository;
    private final SecondCategoryRepository secondCategoryRepository;
    private final SecondRegionRepository secondRegionRepository;
    private final UploadFileRepository uploadFileRepository;
    private final PostVisibilityHistoryRepository postVisibilityHistoryRepository;

    private final PostMapper postMapper;
    private final SupabaseStorageClient storageClient;

    // 공개 글만, 최신순 20개 고정
    public PageResponse<PostSummaryResponse> getPublicPosts(PostSearchCondition condition) {
        return searchPosts(condition, true, latestFirst(condition.pageNumber()));
    }

    // 숨김 글 포함(isVisible로 선택 가능), 페이지 크기·정렬 지정 가능
    public PageResponse<PostSummaryResponse> getAdminPosts(PostSearchCondition condition) {

        Sort.Direction direction = "asc".equals(condition.sortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = switch (condition.sortBy()) {
            case "createdAt", "viewCount", "commentCount" -> Sort.by(direction, condition.sortBy());
            // ponytail: likeCount는 아직 저장하지 않아 모두 0 → 값이 같을 때 규칙(postId 내림차순)만 적용. 좋아요 도메인 구현 후 정렬 추가
            default -> Sort.unsorted();
        };

        // 값이 같은 글은 postId 내림차순
        Pageable pageable = PageRequest.of(condition.pageNumber() - 1, condition.pageSize(),
                sort.and(Sort.by(Sort.Direction.DESC, "id")));

        return searchPosts(condition, condition.isVisible(), pageable);
    }

    private PageResponse<PostSummaryResponse> searchPosts(
            PostSearchCondition condition, @Nullable Boolean isVisible, Pageable pageable) {

        // 소분류가 있다면 대분류는 무시 (지역도 동일)
        Long firstCategoryId = condition.secondCategoryId() != null ? null : condition.firstCategoryId();
        Long firstRegionId = condition.secondRegionId() != null ? null : condition.firstRegionId();

        validateExists(firstCategoryRepository, firstCategoryId);
        validateExists(secondCategoryRepository, condition.secondCategoryId());
        validateExists(firstRegionRepository, firstRegionId);
        validateExists(secondRegionRepository, condition.secondRegionId());

        Page<Post> page = postRepository.findPosts(
                condition.boardType(),
                isVisible,
                condition.keyword(),
                firstCategoryId,
                condition.secondCategoryId(),
                firstRegionId,
                condition.secondRegionId(),
                pageable);

        return toSummaryPage(page);
    }

    // 내가 쓴 글 (숨김·삭제 글 제외, 최신순 20개 고정)
    public PageResponse<PostSummaryResponse> getMyPosts(Long memberId, PostSearchCondition condition) {

        Page<Post> page = postRepository.findMyPosts(memberId, condition.boardType(), latestFirst(condition.pageNumber()));

        return toSummaryPage(page);
    }

    @Transactional
    public PostResponse getPostById(Long postId, @Nullable MemberRole role) {

        Post post = findPost(postId);

        // 숨김 글은 관리자만 조회 (404로 처리하여 일반 사용자가 숨김 사실을 모르게)
        if (!post.isVisible() && role != MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
        }

        postRepository.increaseViewCount(postId);

        return toResponse(post).toBuilder()
                .viewCount(post.getViewCount() + 1)
                .build();
    }

    @Transactional
    public PostResponse createPost(Long memberId, PostRequest postRequest) {

        // 조회 및 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        List<Long> imageIds = Objects.requireNonNullElse(postRequest.getImageIds(), List.of());

        validateBoardPolicy(postRequest.getBoardType(), member, imageIds);

        SecondCategory category = findCategory(postRequest.getSecondCategoryId());

        SecondRegion region = findRegion(postRequest.getSecondRegionId());

        List<UploadFile> images = loadImages(memberId, imageIds);

        // 게시글 엔티티
        Post post = postRepository.save(Post.builder()
                .member(member)
                .secondCategory(category)
                .secondRegion(region)
                .boardType(postRequest.getBoardType())
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .images(images)
                .build());

        return toResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long memberId, PostUpdateRequest postUpdateRequest) {

        Post post = findPost(postId);

        // 숨김 글은 작성자도 수정 불가
        if (!post.isVisible()) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
        }

        // 작성자 본인만 수정 가능 (관리자 포함 타인은 403)
        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        List<Long> imageIds = Objects.requireNonNullElse(postUpdateRequest.getImageIds(), List.of());

        // 게시판 정책 (TIP/PROMOTION 은 EXPERT 만, TIP 은 이미지 필수)
        validateBoardPolicy(post.getBoardType(), post.getMember(), imageIds);

        SecondCategory category = findCategory(postUpdateRequest.getSecondCategoryId());

        SecondRegion region = findRegion(postUpdateRequest.getSecondRegionId());

        List<UploadFile> images = loadImages(memberId, imageIds);

        post.update(category, region, postUpdateRequest.getTitle(), postUpdateRequest.getContent(), images);

        return toResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, Long memberId, MemberRole role) {

        Post post = findPost(postId);

        if (role != MemberRole.ADMIN) {
            // 숨김 글은 관리자만 삭제 (작성자도 404)
            if (!post.isVisible()) {
                throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
            }
            if (!post.getMember().getId().equals(memberId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
            }
            // TIP/PROMOTION 은 작성자가 현재도 고수여야 삭제 가능
            if ((post.getBoardType() == BoardType.TIP || post.getBoardType() == BoardType.PROMOTION) && post.getMember().getRole() != MemberRole.EXPERT) {
                throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
            }
        }

        post.delete();
    }

    // 원하는 상태를 직접 지정 (토글 아님). 상태가 실제로 바뀔 때만 이력 저장
    @Transactional
    public PostVisibilityResponse changeVisibility(Long postId, Long adminId, PostVisibilityRequest request) {

        Post post = findPost(postId);
        boolean isVisible = request.getIsVisible();

        if (post.isVisible() != isVisible) {
            // updatedAt은 글 내용 수정 시각이므로 건드리지 않는다
            postRepository.updateVisibility(postId, isVisible);

            postVisibilityHistoryRepository.save(PostVisibilityHistory.builder()
                    .post(post)
                    .admin(memberRepository.getReferenceById(adminId))
                    .isVisible(isVisible)
                    .reason(request.getReason())
                    .build());
        }

        return new PostVisibilityResponse(postId, isVisible);
    }

    // 삭제된 글의 이력도 조회 가능
    public PostVisibilityHistoryResponse getVisibilityHistory(Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION);
        }

        List<PostVisibilityHistoryResponse.History> histories = postVisibilityHistoryRepository
                .findByPostIdOrderByCreatedAtDescIdDesc(postId).stream()
                .map(postMapper::toHistory)
                .toList();

        return new PostVisibilityHistoryResponse(postId, histories);
    }

    // 삭제된 글은 없는 글과 동일하게 404
    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .filter(post -> post.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
    }

    private @Nullable SecondRegion findRegion(@Nullable Long regionId) {
        return regionId == null ? null
                : secondRegionRepository.findById(regionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));
    }

    private @Nullable SecondCategory findCategory(@Nullable Long categoryId) {
        return categoryId == null ? null
                : secondCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
    }

    private PostResponse toResponse(Post post) {
        // 게시글 이미지 목록
        List<PostResponse.PostImage> imageResponses = post.getImages().stream()
                .map(file -> new PostResponse.PostImage(
                        file.getId(), storageClient.createUrl(file.getVisibility(), file.getStoragePath())))
                .toList();

        // 작성자 프로필 사진 URL
        UploadFile profile = post.getMember().getImage();
        String profileImageUrl = profile == null ? null
                : storageClient.createUrl(profile.getVisibility(), profile.getStoragePath());

        return postMapper.toResponse(post, imageResponses, profileImageUrl);
    }

    // 사용자는 1페이지부터, Spring은 0부터 셈. 크기 20·최신순 고정
    private static Pageable latestFirst(int pageNumber) {
        return PageRequest.of(pageNumber - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private PageResponse<PostSummaryResponse> toSummaryPage(Page<Post> page) {
        return PageResponse.of(page, page.getContent().stream()
                .map(post -> postMapper.toSummary(post, thumbnailUrl(post)))
                .toList());
    }

    // 첫 번째 이미지 URL, 이미지가 없으면 null
    private @Nullable String thumbnailUrl(Post post) {
        if (post.getImages().isEmpty()) {
            return null;
        }
        UploadFile first = post.getImages().getFirst();
        return storageClient.createUrl(first.getVisibility(), first.getStoragePath());
    }

    private static void validateBoardPolicy(BoardType boardType, Member member, List<Long> imageIds) {
        if ((boardType == BoardType.TIP || boardType == BoardType.PROMOTION)
                && member.getRole() != MemberRole.EXPERT) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        if (boardType == BoardType.TIP && imageIds.isEmpty()) {
            throw new BusinessException(ErrorCode.POST_IMAGE_REQUIRED_EXCEPTION);
        }
    }

    private static void validateExists(
            JpaRepository<?, Long> repository,
            @Nullable Long id) {
        if (id != null && !repository.existsById(id)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION);
        }
    }

    // 내가 올린 공개 파일만, 요청한 순서대로 (댓글 이미지에서도 사용)
    public List<UploadFile> loadImages(Long memberId, List<Long> imageIds) {
        List<UploadFile> found = uploadFileRepository.findAllById(imageIds);

        if (found.size() != imageIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_POST_IMAGE_EXCEPTION);
        }
        for (UploadFile file : found) {
            if (!file.getUploader().getId().equals(memberId)
                    || file.getVisibility() != UploadFileVisibility.PUBLIC
                    || file.getDeletedAt() != null) {
                throw new BusinessException(ErrorCode.INVALID_POST_IMAGE_EXCEPTION);
            }
        }

        Map<Long, UploadFile> fileById = found.stream()
                .collect(Collectors.toMap(UploadFile::getId, file -> file));

        return imageIds.stream().map(fileById::get).toList();
    }
}
