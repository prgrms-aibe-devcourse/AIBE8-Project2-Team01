package com.grepp.efy.domain.reaction.bookmark.docs;

import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkCancelResponse;
import com.grepp.efy.domain.reaction.bookmark.dto.BookmarkResponse;
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
