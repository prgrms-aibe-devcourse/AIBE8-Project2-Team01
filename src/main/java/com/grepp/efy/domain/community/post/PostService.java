package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryRepository;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionRepository;
import com.grepp.efy.domain.community.post.dto.PostRequest;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.file.UploadFileRepository;
import com.grepp.efy.domain.file.UploadFileVisibility;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRepository;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
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
    private final SecondCategoryRepository categoryRepository;
    private final SecondRegionRepository regionRepository;
    private final UploadFileRepository uploadFileRepository;

    private final PostMapper postMapper;
    private final SupabaseStorageClient storageClient;

    @Transactional
    public PostResponse getPostById(Long postId, @Nullable MemberRole role) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

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

        Long categoryId = postRequest.getSecondCategoryId();
        SecondCategory category = categoryId == null ? null
                : categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

        Long regionId = postRequest.getSecondRegionId();
        SecondRegion region = regionId == null ? null
                : regionRepository.findById(regionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));

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

    private static void validateBoardPolicy(BoardType boardType, Member member, List<Long> imageIds) {
        if ((boardType == BoardType.TIP || boardType == BoardType.PROMOTION)
                && member.getRole() != MemberRole.EXPERT) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        if (boardType == BoardType.TIP && imageIds.isEmpty()) {
            throw new BusinessException(ErrorCode.POST_IMAGE_REQUIRED_EXCEPTION);
        }
    }

    private List<UploadFile> loadImages(Long memberId, List<Long> imageIds) {
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
