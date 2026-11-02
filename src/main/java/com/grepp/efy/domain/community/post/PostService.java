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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;

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

    @Transactional
    public PostResponse createPost(Long memberId, PostRequest postRequest) {

        // 조회 및 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        Long categoryId = postRequest.getSecondCategoryId();
        SecondCategory category = categoryId == null ? null
                : categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

        Long regionId = postRequest.getSecondRegionId();
        SecondRegion region = regionId == null ? null
                : regionRepository.findById(regionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND_EXCEPTION));

        List<Long> imageIds = Objects.requireNonNullElse(postRequest.getImageIds(), List.of());

        List<UploadFile> found = uploadFileRepository.findAllById(imageIds);

        if(found.size() != imageIds.size()) {
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
        List<UploadFile> images = imageIds.stream().map(fileById::get).toList();

        BoardType boardType = postRequest.getBoardType();
        if((boardType == BoardType.TIP || boardType == BoardType.PROMOTION)
                && member.getRole() != MemberRole.EXPERT) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        if(boardType == BoardType.TIP && imageIds.isEmpty()) {
            throw new BusinessException(ErrorCode.POST_IMAGE_REQUIRED_EXCEPTION);
        }


        return null;
    }
}
