package com.grepp.efy.domain.community.post.docs;

import com.grepp.efy.domain.community.post.BoardType;
import com.grepp.efy.domain.community.post.dto.PostRequest;
import com.grepp.efy.domain.community.post.dto.PostResponse;
import com.grepp.efy.domain.community.post.dto.PostSummaryResponse;
import com.grepp.efy.domain.community.post.dto.PostUpdateRequest;
import com.grepp.efy.domain.community.post.dto.PostVisibilityHistoryResponse;
import com.grepp.efy.domain.community.post.dto.PostVisibilityRequest;
import com.grepp.efy.domain.community.post.dto.PostVisibilityResponse;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;

@Tag(name = "커뮤니티 게시글", description = "커뮤니티 게시글 API")
public interface PostControllerDocs {

    @Operation(summary = "공개 게시글 목록 조회",
            description = "토큰 없이 조회할 수 있습니다. 게시판별 글을 최신순으로 20개씩 반환하며, 페이지 크기와 정렬은 바꿀 수 없습니다. "
                    + "숨김 처리된 글과 삭제된 글은 목록과 totalElements에서 제외합니다. "
                    + "keyword는 제목 또는 본문에 포함된 글을 찾습니다. "
                    + "1차 카테고리·지역을 지정하면 하위 2차 전체가 대상이며, 1차와 2차를 함께 보내면 2차가 우선합니다. "
                    + "결과가 없거나 마지막 페이지를 넘으면 빈 content로 200을 반환합니다. "
                    + "thumbnailUrl은 첫 번째 이미지 URL이며 이미지가 없으면 null, "
                    + "카테고리·지역을 지정하지 않은 글은 category·region이 null입니다. "
                    + "수정되지 않은 글의 updatedAt은 createdAt과 같습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 게시글 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "boardType 누락·오류, pageNumber 오류, keyword 오류, 존재하지 않는 카테고리·지역 ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "타입 오류 (잘못된 boardType, 정수가 아닌 pageNumber·ID)", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "입력값 오류 (boardType 누락, pageNumber 1 미만, keyword 공백·길이, 없는 카테고리·지역)", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """)
            }))
    ResponseEntity<PageResponse<PostSummaryResponse>> getPublicPosts(
            @Parameter(description = "게시판 종류", required = true) BoardType boardType,
            @Parameter(description = "페이지 번호 (1부터, 기본값 1)") @Positive Integer pageNumber,
            @Parameter(description = "제목·본문 검색어 (1~50자)") @Size(min = 1, max = 50) @Pattern(regexp = ".*\\S.*") String keyword,
            @Parameter(description = "1차 카테고리 ID") Long firstCategoryId,
            @Parameter(description = "2차 카테고리 ID (1차보다 우선)") Long secondCategoryId,
            @Parameter(description = "1차 지역(시/도) ID") Long firstRegionId,
            @Parameter(description = "2차 지역(시/군/구) ID (1차보다 우선)") Long secondRegionId);

    @Operation(summary = "관리자 게시글 목록 조회",
            description = "관리자만 조회할 수 있습니다. 숨김 처리된 글을 포함한 모든 글을 반환하며, 삭제된 글은 제외합니다. "
                    + "isVisible로 공개·숨김 글만 골라 볼 수 있고, 생략하면 전체입니다. boardType을 생략하면 전체 게시판입니다. "
                    + "keyword·카테고리·지역 필터는 공개 게시글 목록과 같습니다. "
                    + "정렬은 sortBy·sortOrder로 지정하며, 값이 같은 글은 postId 내림차순으로 정렬합니다. "
                    + "likeCount는 아직 집계하지 않아 모두 0이므로, 이 값으로 정렬하면 postId 내림차순과 같습니다. "
                    + "결과가 없거나 마지막 페이지를 넘으면 빈 content로 200을 반환합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공. 게시글 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "파라미터 값 오류, 존재하지 않는 카테고리·지역 ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "타입 오류 (잘못된 boardType·isVisible, 정수가 아닌 pageNumber·pageSize·ID)", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "입력값 오류 (pageNumber 1 미만, pageSize 1~100 밖, sortBy·sortOrder 오류, keyword 공백·길이, 없는 카테고리·지역)", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "토큰이 없거나 만료·유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "관리자가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<PageResponse<PostSummaryResponse>> getAdminPosts(
            @Parameter(description = "페이지 번호 (1부터, 기본값 1)") @Positive Integer pageNumber,
            @Parameter(description = "페이지 크기 (1~100, 기본값 20)") @Min(1) @Max(100) Integer pageSize,
            @Parameter(description = "게시판 종류 (생략하면 전체)") BoardType boardType,
            @Parameter(description = "제목·본문 검색어 (1~50자)") @Size(min = 1, max = 50) @Pattern(regexp = ".*\\S.*") String keyword,
            @Parameter(description = "1차 카테고리 ID") Long firstCategoryId,
            @Parameter(description = "2차 카테고리 ID (1차보다 우선)") Long secondCategoryId,
            @Parameter(description = "1차 지역(시/도) ID") Long firstRegionId,
            @Parameter(description = "2차 지역(시/군/구) ID (1차보다 우선)") Long secondRegionId,
            @Parameter(description = "정렬 기준: createdAt(기본값), commentCount, likeCount, viewCount") @Pattern(regexp = "createdAt|commentCount|likeCount|viewCount") String sortBy,
            @Parameter(description = "정렬 방향: asc, desc(기본값)") @Pattern(regexp = "asc|desc") String sortOrder,
            @Parameter(description = "공개 여부 (생략하면 전체)") Boolean isVisible);

    @Operation(summary = "내 게시글 목록 조회",
            description = "토큰으로 식별한 본인이 쓴 글을 최신순으로 20개씩 반환하며, 페이지 크기와 정렬은 바꿀 수 없습니다. "
                    + "boardType을 생략하면 전체 게시판의 글을 반환합니다. "
                    + "숨김 처리된 글과 삭제된 글은 목록과 totalElements에서 제외합니다. "
                    + "결과가 없거나 마지막 페이지를 넘으면 빈 content로 200을 반환합니다. "
                    + "응답 항목 규칙(thumbnailUrl, category, region, updatedAt)은 공개 게시글 목록과 같습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공. 게시글 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "boardType 오류, pageNumber 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "타입 오류 (잘못된 boardType, 정수가 아닌 pageNumber)", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "입력값 오류 (pageNumber 1 미만)", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "토큰이 없거나 만료·유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<PageResponse<PostSummaryResponse>> getMyPosts(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "게시판 종류 (생략하면 전체)") BoardType boardType,
            @Parameter(description = "페이지 번호 (1부터, 기본값 1)") @Positive Integer pageNumber);

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

    @Operation(summary = "게시글 삭제",
            description = "작성자 본인과 관리자만 삭제할 수 있습니다. 관리자는 숨김 글을 포함해 모든 글을 삭제할 수 있습니다. "
                    + "숨김 처리된 글은 관리자가 아니면 작성자도 삭제할 수 없고 404로 응답합니다. "
                    + "TIP·PROMOTION 글은 작성자가 현재도 고수여야 삭제할 수 있으며, 관리자에게는 이 조건을 적용하지 않습니다. "
                    + "논리 삭제 방식으로 삭제 시각만 기록하며, 삭제된 글은 이후 모든 조회에서 제외되고 복구할 수 없습니다. "
                    + "삭제된 글의 댓글·좋아요·이미지는 그대로 남습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
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
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "작성자도 관리자도 아닌 회원의 삭제, 또는 고수가 아닌 작성자의 TIP·PROMOTION 글 삭제",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 없음", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 게시글, 또는 관리자가 아닌 회원의 숨김 글 삭제",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """)
            }))
    ResponseEntity<Void> deletePost(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId);

    @Operation(summary = "관리자 게시글 공개/비공개 전환",
            description = "관리자만 호출할 수 있습니다. 토글이 아니라 원하는 상태를 isVisible로 직접 지정하며, "
                    + "이미 같은 상태여도 200으로 응답합니다. "
                    + "상태가 실제로 바뀐 경우에만 변경 이력(관리자, 변경 시각, 변경 후 상태, 사유)을 저장합니다. "
                    + "공개 여부만 바꾸며 글의 updatedAt은 갱신하지 않습니다. 삭제된 글은 바꿀 수 없습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "변경 성공 (같은 상태 요청 포함)",
            content = @Content(schema = @Schema(implementation = PostVisibilityResponse.class)))
    @ApiResponse(responseCode = "400", description = "postId 오류, isVisible 누락·형식 오류, reason 길이 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 오류 (postId 1 미만, isVisible 누락, reason 공백·200자 초과)", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "본문 형식 오류 (isVisible이 true/false가 아님)", value = """
                            {
                              "message": "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "관리자가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 게시글",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "게시글 없음", value = """
                            {
                              "message": "존재하지 않는 게시물입니다."
                            }
                            """)
            }))
    ResponseEntity<PostVisibilityResponse> changeVisibility(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long postId,
            @Valid PostVisibilityRequest request);

    @Operation(summary = "관리자 게시글 공개/비공개 변경 이력 조회",
            description = "관리자만 호출할 수 있습니다. 변경 이력을 최신순(같은 시각이면 historyId 내림차순)으로 페이지 없이 모두 반환합니다. "
                    + "삭제된 글의 이력도 조회할 수 있습니다. 이력이 없으면 histories는 빈 배열, 사유가 없으면 reason은 null입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PostVisibilityHistoryResponse.class)))
    @ApiResponse(responseCode = "400", description = "postId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "관리자가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<PostVisibilityHistoryResponse> getVisibilityHistory(
            @Positive Long postId);
}
