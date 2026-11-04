package com.grepp.efy.domain.reaction.bookmark.docs;

import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkCancelResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkSummaryResponse;
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
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "북마크", description = "고수 프로필 북마크(찜) API")
public interface BookmarkControllerDocs {

    @Operation(summary = "내 북마크 목록 조회",
            description = "내가 북마크한 고수 프로필을 최신 북마크순(같으면 북마크 ID 내림차순)으로 20개씩 반환합니다. 페이지 크기는 바꿀 수 없습니다. "
                    + "취소한 북마크와 탈퇴한 고수의 북마크는 제외하며, totalElements도 같은 기준으로 셉니다. "
                    + "category는 고수 프로필의 대표 카테고리입니다. "
                    + "활동 지역은 고수 프로필에 저장된 그대로 nationwide(전국 여부), wholeRegions(시/도 전체, ID 오름차순), "
                    + "regions(개별 시/군/구, ID 오름차순)를 모두 반환하며, 어떻게 보여줄지는 프론트에서 결정합니다. "
                    + "averageRating·reviewCount는 리뷰 기능 구현 전까지 null·0입니다. "
                    + "프로필 사진이 없으면 expertProfileImageUrl은 null이고, 북마크가 없거나 마지막 페이지를 넘으면 빈 content로 200을 반환합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공. 북마크 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "pageNumber가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    ResponseEntity<PageResponse<BookmarkSummaryResponse>> getMyBookmarks(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터, 기본값 1)") @Positive Integer pageNumber);

    @Operation(summary = "북마크 등록",
            description = "로그인한 회원이 고수 프로필을 북마크합니다. 요청 본문은 없습니다. "
                    + "같은 고수 프로필은 한 번만 북마크할 수 있으며, 자신의 고수 프로필도 북마크할 수 있습니다. "
                    + "취소한 기록이 있으면 다시 활성으로 바꾸고 bookmarkedAt을 지금 시각으로 갱신합니다. "
                    + "탈퇴한 고수의 프로필은 없는 프로필로 취급합니다. "
                    + "회원당 북마크는 최대 500개이며, 탈퇴한 고수의 북마크는 개수에 포함하지 않습니다. "
                    + "이미 북마크한 고수는 개수와 관계없이 중복 오류를 먼저 반환합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = BookmarkResponse.class)))
    @ApiResponse(responseCode = "400", description = "expertProfileId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 탈퇴한 고수 프로필",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "고수 프로필 없음", value = """
                            {
                              "message": "고수 프로필을 찾을 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "409", description = "이미 북마크함, 또는 북마크가 500개에 도달함",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "이미 북마크함", value = """
                            {
                              "message": "이미 북마크한 고수입니다."
                            }
                            """),
                    @ExampleObject(name = "개수 초과", value = """
                            {
                              "message": "북마크는 최대 500개까지 등록할 수 있습니다."
                            }
                            """)
            }))
    ResponseEntity<BookmarkResponse> createBookmark(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long expertProfileId);

    @Operation(summary = "북마크 취소",
            description = "로그인한 회원이 등록한 고수 프로필 북마크를 취소합니다. 요청 본문은 없습니다. "
                    + "기록은 지우지 않고 비활성으로 바꾸며, 취소한 뒤 같은 고수 프로필을 다시 북마크할 수 있습니다. "
                    + "북마크하지 않았거나 이미 취소한 경우 404로 응답합니다. "
                    + "탈퇴한 고수의 북마크는 취소할 수 없고 404로 응답합니다(등록과 같은 규칙).")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "취소 성공",
            content = @Content(schema = @Schema(implementation = BookmarkCancelResponse.class)))
    @ApiResponse(responseCode = "400", description = "expertProfileId가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 탈퇴한 고수 프로필, 또는 활성 북마크가 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "고수 프로필 없음", value = """
                            {
                              "message": "고수 프로필을 찾을 수 없습니다."
                            }
                            """),
                    @ExampleObject(name = "북마크 없음", value = """
                            {
                              "message": "존재하지 않는 북마크입니다."
                            }
                            """)
            }))
    ResponseEntity<BookmarkCancelResponse> cancelBookmark(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long expertProfileId);
}
