package com.grepp.efy.domain.basedata.firstregion.docs;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "시·도", description = "시·도 공개 조회 및 관리자 관리 API")
public interface FirstRegionControllerDocs {

    @SecurityRequirements
    @Operation(summary = "시·도 목록 조회", description = "로그인 없이 시·도 목록을 ID 오름차순으로 조회합니다. 조회 결과가 없으면 빈 배열을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 목록 반환")
    @ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"서버 오류가 발생했습니다.\"}")))
    ResponseEntity<List<FirstRegionResponse>> getFirstRegions();

    @Operation(summary = "시·도 등록", description = "관리자만 호출할 수 있습니다. 시·도 항목을 등록합니다. 시·도 전체에서 이름의 대소문자를 구분하지 않고 중복을 검사합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공. 등록된 항목 반환")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 본문 누락·잘못된 JSON",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"입력값이 올바르지 않습니다.\"}")))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"인증에 실패했습니다.\"}")))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"접근 권한이 없습니다.\"}")))
    @ApiResponse(responseCode = "409", description = "이름 또는 코드 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"같은 상위 항목에 이미 사용 중인 지역 이름입니다.\"}")))
    ResponseEntity<FirstRegionResponse> createFirstRegion(FirstRegionRequest request);

    @Operation(summary = "시·도 수정", description = "관리자만 호출할 수 있습니다. 시·도의 이름·코드를 수정합니다. 시·도 전체에서 이름의 대소문자를 구분하지 않고 중복을 검사합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공. 수정된 항목 반환")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 본문 누락·잘못된 JSON·ID 형식 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"입력값이 올바르지 않습니다.\"}")))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"인증에 실패했습니다.\"}")))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"접근 권한이 없습니다.\"}")))
    @ApiResponse(responseCode = "404", description = "수정 대상이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"지역을 찾을 수 없습니다.\"}")))
    @ApiResponse(responseCode = "409", description = "이름 또는 코드 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"같은 상위 항목에 이미 사용 중인 지역 이름입니다.\"}")))
    ResponseEntity<FirstRegionResponse> updateFirstRegion(Long firstRegionId, FirstRegionRequest request);

    @Operation(summary = "시·도 삭제", description = "관리자만 호출할 수 있습니다. 시·도 항목을 물리 삭제합니다. 하위 항목이 있거나 다른 데이터에서 참조 중이면 삭제를 거절합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공. 삭제한 항목 반환")
    @ApiResponse(responseCode = "400", description = "삭제 대상 ID가 올바른 숫자 형식이 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"경로 또는 쿼리 값의 타입이 올바르지 않습니다.\"}")))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"인증에 실패했습니다.\"}")))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"접근 권한이 없습니다.\"}")))
    @ApiResponse(responseCode = "404", description = "삭제 대상이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"지역을 찾을 수 없습니다.\"}")))
    @ApiResponse(responseCode = "409", description = "하위 항목 또는 다른 데이터에서 참조 중",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"데이터 무결성 제약 조건 위반입니다.\"}")))
    ResponseEntity<FirstRegionResponse> deleteFirstRegion(Long firstRegionId);
}
