package com.grepp.efy.domain.user.expertprofile.docs;

import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.grepp.efy.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "고수 프로필", description = "고수 프로필 등록 및 조회 API")
public interface ExpertProfileControllerDocs {

    @Operation(summary = "고수 프로필 공개 조회",
            description = "로그인 없이 프로필 ID로 조회합니다. 소개·경력·제공 서비스·대표 서비스·활동 지역을 반환하며 이메일·전화번호는 포함하지 않습니다. "
                    + "현재 역할이 USER여도 조회할 수 있지만 탈퇴 회원의 프로필은 조회할 수 없습니다.")
    @ApiResponse(responseCode = "200", description = "고수 프로필 조회 성공",
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

    @Operation(summary = "첫 고수 프로필 등록",
            description = "인증된 USER의 본인 프로필을 등록하고 EXPERT로 전환합니다. 실패하면 전체 작업을 취소합니다. "
                    + "대표 서비스는 제공 서비스에 포함해야 합니다. 전국과 개별 지역은 함께 선택할 수 없으며 "
                    + "시도 전체와 같은 시도의 시군구도 함께 선택할 수 없습니다. 사진 등록은 후속 작업입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "등록 및 고수 전환 성공",
            content = @Content(schema = @Schema(implementation = ExpertProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 또는 서비스·지역 선택 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "USER 이외 역할은 등록 불가")
    @ApiResponse(responseCode = "404", description = "회원·카테고리·지역을 찾을 수 없음")
    @ApiResponse(responseCode = "409", description = "이미 등록된 고수 프로필")
    ResponseEntity<ExpertProfileResponse> createExpertProfile(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            ExpertProfileRequest request);

}
