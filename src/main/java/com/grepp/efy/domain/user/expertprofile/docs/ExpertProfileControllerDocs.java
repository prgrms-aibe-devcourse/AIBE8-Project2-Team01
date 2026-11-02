package com.grepp.efy.domain.user.expertprofile.docs;

import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileCreateRequest;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileResponse;
import com.grepp.efy.domain.user.expertprofile.dto.ExpertProfileUpdateRequest;
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

@Tag(name = "고수 프로필", description = "고수 프로필 등록·조회·수정 API")
public interface ExpertProfileControllerDocs {

    @Operation(summary = "고수 프로필 공개 조회",
            description = "로그인 없이 프로필 ID로 조회합니다. 소개·경력·제공 서비스·대표 서비스·활동 지역과 회원 공통 프로필 이미지 파일 ID(imageId)와 공개 URL(imageUrl)을 반환합니다. 사진이 없으면 두 값은 null이며 삭제되거나 비공개인 사진의 URL은 반환하지 않습니다. 이메일·전화번호는 포함하지 않습니다. "
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
                    + "시도 전체와 같은 시도의 시군구도 함께 선택할 수 없습니다. 공통 파일 업로드 후 받은 imageId로 본인의 PUBLIC 파일을 회원 공통 프로필 사진으로 연결할 수 있습니다. imageId를 생략하면 기존 사진을 유지합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "등록 및 고수 전환 성공",
            content = @Content(schema = @Schema(implementation = ExpertProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 또는 서비스·지역 선택 오류, 비공개 이미지 선택",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "USER 이외 역할 또는 다른 회원의 파일 선택")
    @ApiResponse(responseCode = "404", description = "회원·카테고리·지역·파일을 찾을 수 없음 또는 삭제된 파일")
    @ApiResponse(responseCode = "409", description = "이미 등록된 고수 프로필")
    ResponseEntity<ExpertProfileResponse> createExpertProfile(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            ExpertProfileCreateRequest request);

    @Operation(summary = "본인 고수 프로필 수정",
            description = "USER·EXPERT가 본인 프로필을 부분 수정합니다. 생략한 항목은 유지하며 경력 연수·경력 설명은 명시적인 null로 삭제할 수 있습니다. "
                    + "다른 항목의 null은 기존 값을 유지합니다. 서비스와 지역 목록은 보내면 해당 목록 전체를 교체합니다. "
                    + "전국으로 변경할 때 기존 지역을 지우려면 wholeRegionIds와 regionIds를 빈 배열로 함께 보냅니다. "
                    + "최종 선택 조합은 등록과 같은 규칙으로 검사하며 실패하면 기존 데이터를 유지합니다. 사진 수정은 후속 작업입니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "수정된 고수 프로필 반환",
            content = @Content(schema = @Schema(implementation = ExpertProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 또는 최종 서비스·지역 선택 조합 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음")
    @ApiResponse(responseCode = "403", description = "USER·EXPERT 이외 역할은 수정 불가")
    @ApiResponse(responseCode = "404", description = "본인 프로필·회원·선택한 카테고리·지역을 찾을 수 없음")
    ResponseEntity<ExpertProfileResponse> updateExpertProfile(
            @Parameter(hidden = true) CustomUserDetails userDetails, ExpertProfileUpdateRequest request);

}
