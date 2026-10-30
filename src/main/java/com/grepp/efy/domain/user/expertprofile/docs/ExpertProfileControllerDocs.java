package com.grepp.efy.domain.user.expertprofile.docs;

import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "고수 프로필", description = "고수 프로필 조회 API")
public interface ExpertProfileControllerDocs {

    @Operation(summary = "고수 프로필 공개 조회",
            description = "로그인 없이 프로필 ID로 조회합니다. 현재는 소개와 경력 정보를 반환하며 이메일·전화번호는 포함하지 않습니다. "
                    + "현재 역할이 USER여도 조회할 수 있지만 탈퇴 회원의 프로필은 조회할 수 없습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 프로필 ID·소개·경력 정보 반환",
            content = @Content(schema = @Schema(implementation = ExpertProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "프로필 ID가 Long으로 변환할 수 없는 값",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "ID 형식 오류", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "프로필이 없거나 연결된 회원이 없거나 탈퇴한 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "프로필 없음", value = """
                            {
                              "message": "고수 프로필을 찾을 수 없습니다."
                            }
                            """),
                    @ExampleObject(name = "연결 회원 없음 또는 탈퇴", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<ExpertProfileResponse> getExpertProfile(
            @Parameter(description = "고수 프로필 ID", required = true, example = "1")
            Long expertProfileId);
}
