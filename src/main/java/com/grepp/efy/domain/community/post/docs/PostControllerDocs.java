package com.grepp.efy.domain.community.post.docs;

import com.grepp.efy.domain.community.post.dto.PostRequest;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.community.post.dto.PostUpdateRequest;
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

@Tag(name = "커뮤니티 게시글", description = "커뮤니티 게시글 API")
public interface PostControllerDocs {

    @Operation(summary = "게시글 상세 조회",
            description = "토큰 없이 조회할 수 있으며, 만료되었거나 유효하지 않은 토큰은 비로그인으로 취급합니다. "
                    + "숨김 처리된 글은 관리자만 조회할 수 있고, 작성자를 포함한 그 외 사용자에게는 404로 응답합니다. "
                    + "조회할 때마다 조회수가 1 증가하며(중복 방지 없음), 응답에는 증가가 반영된 값을 담습니다. "
                    + "이미지가 없으면 images는 빈 배열, 프로필 사진이 없으면 memberProfileImageUrl은 null, "
                    + "카테고리·지역을 지정하지 않은 글은 category·region이 null입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = "postId가 정수가 아니거나 1 미만",
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
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글, 또는 관리자가 아닌 사용자의 숨김 글 조회",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """)
            }))
    ResponseEntity<PostResponse> getPostById(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId);

    @Operation(summary = "게시글 작성",
            description = "이미지는 먼저 POST /api/files로 PUBLIC 업로드한 뒤 받은 fileId를 imageIds에 원하는 순서대로 넣습니다(최대 10개). "
                    + "본인이 올린 PUBLIC 파일만 사용할 수 있습니다. "
                    + "TIP·PROMOTION 게시판은 고수만 작성할 수 있고, TIP 게시판은 이미지가 1장 이상 필요합니다. "
                    + "카테고리와 지역은 선택이며, 2차 카테고리·2차 지역 ID로 보냅니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "작성 성공",
            content = @Content(schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 오류, 잘못된 JSON·게시판 종류, 사용할 수 없는 이미지, TIP 이미지 누락",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 오류", value = """
                            {
                              "message": "입력값이 올바르지 않습니다.",
                              "errors": {
                                "title": "제목은 2자 이상 30자 이하로 입력해 주세요."
                              }
                            }
                            """),
                    @ExampleObject(name = "잘못된 JSON·게시판 종류", value = """
                            {
                              "message": "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."
                            }
                            """),
                    @ExampleObject(name = "사용할 수 없는 이미지", value = """
                            {
                              "message": "사용할 수 없는 이미지가 포함되어 있습니다."
                            }
                            """),
                    @ExampleObject(name = "TIP 이미지 누락", value = """
                            {
                              "message": "팁 게시글에는 이미지를 1장 이상 첨부해 주세요."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "고수가 아닌 회원이 TIP·PROMOTION 게시판에 작성",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 없음", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "회원·카테고리·지역 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 없음", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """),
                    @ExampleObject(name = "카테고리 없음", value = """
                            {
                              "message": "카테고리를 찾을 수 없습니다."
                            }
                            """),
                    @ExampleObject(name = "지역 없음", value = """
                            {
                              "message": "지역을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<PostResponse> createPost(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Valid PostRequest postRequest);

    @Operation(summary = "게시글 수정",
            description = "작성자 본인만 수정할 수 있으며, 관리자를 포함한 다른 회원은 403입니다. "
                    + "숨김 처리된 글은 작성자도 수정할 수 없고 404로 응답합니다. "
                    + "게시판 종류는 변경할 수 없습니다. "
                    + "요청 내용으로 전체를 교체하므로 secondCategoryId·secondRegionId·imageIds를 생략하면 해당 값이 비워집니다. "
                    + "imageIds는 작성과 같은 규칙(본인이 올린 PUBLIC 파일, 최대 10개, 순서 유지)을 따릅니다. "
                    + "TIP·PROMOTION 글은 고수만 수정할 수 있고, TIP 글은 이미지가 1장 이상 필요합니다. "
                    + "변경된 내용이 없으면 현재 게시글을 그대로 반환하며 updatedAt은 갱신되지 않습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 오류, 잘못된 JSON, postId 오류, 사용할 수 없는 이미지, TIP 이미지 누락",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 오류", value = """
                            {
                              "message": "입력값이 올바르지 않습니다.",
                              "errors": {
                                "title": "제목은 2자 이상 30자 이하로 입력해 주세요."
                              }
                            }
                            """),
                    @ExampleObject(name = "잘못된 JSON", value = """
                            {
                              "message": "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."
                            }
                            """),
                    @ExampleObject(name = "postId가 정수가 아님", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "사용할 수 없는 이미지", value = """
                            {
                              "message": "사용할 수 없는 이미지가 포함되어 있습니다."
                            }
                            """),
                    @ExampleObject(name = "TIP 이미지 누락", value = """
                            {
                              "message": "팁 게시글에는 이미지를 1장 이상 첨부해 주세요."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "작성자가 아닌 회원의 수정, 또는 고수가 아닌 작성자의 TIP·PROMOTION 글 수정",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 없음", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "게시글 없음(숨김 글 포함)·카테고리·지역 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """),
                    @ExampleObject(name = "카테고리 없음", value = """
                            {
                              "message": "카테고리를 찾을 수 없습니다."
                            }
                            """),
                    @ExampleObject(name = "지역 없음", value = """
                            {
                              "message": "지역을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<PostResponse> updatePost(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId,
            @Valid PostUpdateRequest postUpdateRequest);
}
