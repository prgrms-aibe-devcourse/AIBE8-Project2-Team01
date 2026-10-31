package com.grepp.efy.domain.user.member.docs;

import com.grepp.efy.domain.user.member.dto.MemberResponse;
import com.grepp.efy.domain.user.member.dto.MemberRoleUpdateRequest;
import com.grepp.efy.domain.user.member.dto.MemberUpdateRequest;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원", description = "회원 정보 조회·수정·탈퇴 및 관리자 회원 관리 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 목록 조회",
            description = "관리자만 호출할 수 있습니다. 탈퇴하지 않은 회원 목록을 페이지 단위로 반환합니다. 페이지 번호는 1부터 시작합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 회원 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "페이지 번호·크기 또는 정렬 조건이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "페이지 조건 오류", value = """
                            {
                              "message": "페이지 번호, 크기 또는 정렬 조건이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "인증 실패", value = """
                            {
                              "message": "인증에 실패했습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 부족", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    ResponseEntity<PageResponse<MemberResponse>> getMembers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    @Operation(summary = "내 정보 조회",
            description = "로그인한 회원이 자신의 정보를 조회합니다. 비밀번호는 반환하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 본인의 회원 정보 반환")
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "인증 실패", value = """
                            {
                              "message": "인증에 실패했습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "회원이 없거나 탈퇴한 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 없음", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<MemberResponse> getMyData(CustomUserDetails userDetails);

    @Operation(summary = "내 정보 수정",
            description = "로그인한 회원이 자신의 닉네임·전화번호·비밀번호를 수정합니다. 생략하거나 null인 필드는 유지하며 비밀번호 변경 후에도 로그인은 유지됩니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공. 수정된 회원 정보 반환")
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않거나 탈퇴 회원용 예약 닉네임을 사용함",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 검증 실패", value = """
                            {
                              "message": "입력값이 올바르지 않습니다.",
                              "errors": {
                                "password": "비밀번호는 4자 이상 20자 이하로 입력해 주세요."
                              }
                            }
                            """),
                    @ExampleObject(name = "예약 닉네임", value = """
                            {
                              "message": "탈퇴 회원용 이메일이나 닉네임은 사용할 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "인증 실패", value = """
                            {
                              "message": "인증에 실패했습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "회원이 없거나 탈퇴한 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 없음", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "409", description = "다른 회원이 사용 중인 닉네임 또는 전화번호",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "닉네임 중복", value = """
                            {
                              "message": "이미 사용 중인 닉네임입니다."
                            }
                            """),
                    @ExampleObject(name = "전화번호 중복", value = """
                            {
                              "message": "이미 사용 중인 전화번호입니다."
                            }
                            """)
            }))
    ResponseEntity<MemberResponse> updateMember(CustomUserDetails userDetails, MemberUpdateRequest request);

    @Operation(summary = "본인 역할 전환",
            description = "로그인한 회원의 역할을 USER 또는 EXPERT로 전환합니다. EXPERT 전환에는 기존 고수 프로필이 필요합니다. "
                    + "ADMIN은 본인 역할을 전환할 수 없고 ADMIN으로의 전환도 허용하지 않습니다. "
                    + "동일 역할 재요청은 기존 상태를 유지합니다. 프로필과 연관 데이터 및 기존 로그인은 유지하고 다음 요청부터 현재 권한을 적용합니다.")
    @ApiResponse(responseCode = "200", description = "역할 전환 성공 또는 기존 역할 유지",
            content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @ApiResponse(responseCode = "400", description = "역할 누락 또는 잘못된 역할 값",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "ADMIN의 본인 전환 또는 ADMIN으로의 전환 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "회원이 없거나 EXPERT 전환에 필요한 고수 프로필이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<MemberResponse> changeMyRole(
            @Parameter(hidden = true) CustomUserDetails userDetails, MemberRoleUpdateRequest request);

    @Operation(summary = "회원 권한 변경",
            description = "관리자만 호출할 수 있습니다. 다른 회원의 권한을 USER·EXPERT·ADMIN으로 변경합니다. 본인의 관리자 권한은 변경할 수 없으며 대상 회원의 로그인은 유지됩니다.")
    @ApiResponse(responseCode = "200", description = "변경 성공. 변경된 회원 정보 반환")
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않거나 본인의 관리자 권한을 변경하려는 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 누락", value = """
                            {
                              "message": "입력값이 올바르지 않습니다.",
                              "errors": {
                                "role": "권한을 선택해 주세요."
                              }
                            }
                            """),
                    @ExampleObject(name = "본인 권한 변경", value = """
                            {
                              "message": "본인의 관리자 권한은 변경할 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "인증 실패", value = """
                            {
                              "message": "인증에 실패했습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 부족", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "대상 회원이 없거나 탈퇴한 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 없음", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<MemberResponse> setRole(CustomUserDetails userDetails, Long memberId, MemberRoleUpdateRequest request);

    @Operation(summary = "회원 탈퇴",
            description = "로그인한 회원이 본인 계정을 논리 삭제합니다. 개인정보를 정리하고 기존 Access·Refresh 토큰을 차단하며 Refresh 쿠키를 만료 처리합니다.")
    @ApiResponse(responseCode = "204", description = "탈퇴 성공. 응답 본문 없음")
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "인증 실패", value = """
                            {
                              "message": "인증에 실패했습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "회원이 없거나 이미 탈퇴한 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 없음", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<Void> deleteMember(CustomUserDetails userDetails);

    @Operation(summary = "관리자 회원 삭제",
            description = "관리자만 호출할 수 있습니다. 회원을 논리 삭제합니다. 대상 회원의 개인정보를 정리하고 기존 Access·Refresh 토큰을 차단합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공. 응답 본문 없음")
    @ApiResponse(responseCode = "400", description = "회원 ID가 올바른 숫자 형식이 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 ID 형식 오류", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "인증 실패", value = """
                            {
                              "message": "인증에 실패했습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 부족", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "대상 회원이 없거나 이미 탈퇴한 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "회원 없음", value = """
                            {
                              "message": "회원을 찾을 수 없습니다."
                            }
                            """)
            }))
    ResponseEntity<Void> deleteMemberByAdmin(Long memberId);
}
