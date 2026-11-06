package com.grepp.efy.domain.reaction.postlike.docs;

import com.grepp.efy.domain.reaction.postlike.dto.PostCommentLikeResponse;
import com.grepp.efy.domain.reaction.postlike.dto.PostLikeResponse;
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
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "좋아요", description = "게시글·댓글 좋아요 API")
public interface PostLikeControllerDocs {

    @Operation(summary = "게시글 좋아요 등록",
            description = "삭제되지 않은 공개 글만 대상이며, 숨김 글은 관리자를 포함해 404로 응답합니다. "
                    + "한 회원은 한 게시글에 한 번만 좋아요할 수 있고, 본인 글에도 좋아요할 수 있습니다. "
                    + "등록하면 게시글의 likeCount가 1 증가하며, 응답의 likeCount는 이 요청이 반영된 현재 값입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = PostLikeResponse.class)))
    @ApiResponse(responseCode = "400", description = "postId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제·숨김 처리된 게시글",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "409", description = "이미 좋아요한 게시글",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "중복 좋아요", value = """
                            {
                              "message": "이미 좋아요한 게시글입니다."
                            }
                            """)
            }))
    ResponseEntity<PostLikeResponse> likePost(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId);

    @Operation(summary = "게시글 좋아요 취소",
            description = "본인이 등록한 좋아요만 취소할 수 있습니다. 삭제·숨김 글은 404로 응답합니다. "
                    + "취소하면 게시글의 likeCount가 1 감소합니다(0 미만으로 내려가지 않음). "
                    + "다른 삭제 API와 달리 204가 아니라 200으로 응답하며, 취소 후 현재 likeCount를 담습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "취소 성공",
            content = @Content(schema = @Schema(implementation = PostLikeResponse.class)))
    @ApiResponse(responseCode = "400", description = "postId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제·숨김 처리된 게시글, 또는 좋아요한 기록이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """),
                    @ExampleObject(name = "좋아요 기록 없음", value = """
                            {
                              "message": "좋아요한 기록이 없습니다."
                            }
                            """)
            }))
    ResponseEntity<PostLikeResponse> unlikePost(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId);

    @Operation(summary = "댓글 좋아요 등록",
            description = "삭제되지 않은 댓글만 대상이며, 부모 글이 삭제되었거나 숨김이면 관리자를 포함해 404로 응답합니다. "
                    + "한 회원은 한 댓글에 한 번만 좋아요할 수 있고, 본인 댓글에도 좋아요할 수 있습니다. "
                    + "등록하면 댓글의 likeCount가 1 증가하며, 응답의 likeCount는 이 요청이 반영된 현재 값입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = PostCommentLikeResponse.class)))
    @ApiResponse(responseCode = "400", description = "commentId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 댓글, 또는 부모 글이 삭제·숨김 처리됨",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "댓글 없음", value = """
                            {
                              "message": "존재하지 않는 댓글입니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "409", description = "이미 좋아요한 댓글",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "중복 좋아요", value = """
                            {
                              "message": "이미 좋아요한 댓글입니다."
                            }
                            """)
            }))
    ResponseEntity<PostCommentLikeResponse> likeComment(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long commentId);

    @Operation(summary = "댓글 좋아요 취소",
            description = "본인이 등록한 좋아요만 취소할 수 있습니다. 삭제된 댓글, 부모 글이 삭제·숨김인 댓글은 404로 응답합니다. "
                    + "취소하면 댓글의 likeCount가 1 감소합니다(0 미만으로 내려가지 않음). "
                    + "게시글 좋아요 취소와 같이 200으로 응답하며, 취소 후 현재 likeCount를 담습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "취소 성공",
            content = @Content(schema = @Schema(implementation = PostCommentLikeResponse.class)))
    @ApiResponse(responseCode = "400", description = "commentId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 댓글, 부모 글 삭제·숨김, 또는 좋아요한 기록이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "댓글 없음", value = """
                            {
                              "message": "존재하지 않는 댓글입니다."
                            }
                            """),
                    @ExampleObject(name = "좋아요 기록 없음", value = """
                            {
                              "message": "좋아요한 기록이 없습니다."
                            }
                            """)
            }))
    ResponseEntity<PostCommentLikeResponse> unlikeComment(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long commentId);
}
