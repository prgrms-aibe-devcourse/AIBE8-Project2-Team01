package com.grepp.efy.domain.community.comment.docs;

import com.grepp.efy.domain.community.comment.dto.PostCommentRequest;
import com.grepp.efy.domain.community.comment.dto.PostCommentResponse;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "커뮤니티 댓글", description = "커뮤니티 댓글 API")
public interface PostCommentControllerDocs {

    @Operation(summary = "댓글 목록 조회",
            description = "토큰 없이 조회할 수 있으며, 만료되었거나 유효하지 않은 토큰은 비로그인으로 취급합니다. "
                    + "숨김 처리된 글의 댓글은 관리자만 조회할 수 있고, 그 외 사용자에게는 404로 응답합니다. "
                    + "최신순으로 20개씩 반환하며(같은 시각이면 commentId 내림차순), 페이지 크기와 정렬은 바꿀 수 없습니다. "
                    + "삭제된 댓글은 제외하며, totalElements는 게시글의 commentCount와 같습니다. "
                    + "이미지가 없으면 images는 빈 배열, 프로필 사진이 없으면 memberProfileImageUrl은 null입니다. "
                    + "likeCount·liked는 좋아요 기능 구현 전까지 0·false입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공. 댓글 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "postId·pageNumber가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "정수가 아님", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "1 미만", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 게시글, 또는 관리자가 아닌 사용자의 숨김 글 댓글 조회",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """)
            }))
    ResponseEntity<PageResponse<PostCommentResponse>> getComments(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId,
            @Parameter(description = "페이지 번호 (1부터, 기본값 1)") @Positive Integer pageNumber);

    @Operation(summary = "댓글 작성",
            description = "삭제되지 않은 공개 글에만 작성할 수 있으며, 숨김 글은 관리자를 포함해 404로 응답합니다. "
                    + "본인 글에도 작성할 수 있습니다. 작성하면 게시글의 commentCount가 1 증가합니다. "
                    + "imageIds에는 파일 업로드 API(POST /api/files)로 받은 fileId를 최대 1개 담으며, "
                    + "본인이 올린 공개 파일만 사용할 수 있습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "작성 성공",
            content = @Content(schema = @Schema(implementation = PostCommentResponse.class)))
    @ApiResponse(responseCode = "400", description = "postId 오류, content 누락·길이 오류, 이미지 2개 이상, 사용할 수 없는 이미지",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 오류", value = """
                            {
                              "message": "입력값이 올바르지 않습니다.",
                              "errors": {
                                "content": "댓글은 2자 이상 400자 이하로 입력해 주세요."
                              }
                            }
                            """),
                    @ExampleObject(name = "사용할 수 없는 이미지", value = """
                            {
                              "message": "사용할 수 없는 이미지가 포함되어 있습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제·숨김 처리된 게시글",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """)
            }))
    ResponseEntity<PostCommentResponse> createComment(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId,
            @Valid PostCommentRequest request);
}
