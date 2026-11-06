package com.grepp.efy.domain.basedata.secondregion.docs;

import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionCreateRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "시·군·구", description = "시·군·구 공개 조회 및 관리자 관리 API")
public interface SecondRegionControllerDocs {

    @SecurityRequirements
    @Operation(summary = "시·군·구 목록 조회", description = "로그인 없이 시·군·구 목록을 ID 오름차순으로 조회합니다. 조회 결과가 없으면 빈 배열을 반환합니다. 지정한 상위 항목이 없으면 조회를 거절합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 목록 반환")
    @ApiResponse(responseCode = "400", description = "상위 ID가 올바른 숫자 형식이 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"경로 또는 쿼리 값의 타입이 올바르지 않습니다.\"}")))
    @ApiResponse(responseCode = "404", description = "상위 항목이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"지역을 찾을 수 없습니다.\"}")))
    ResponseEntity<List<SecondRegionResponse>> getSecondRegions(Long firstRegionId);

    @Operation(summary = "시·군·구 등록", description = "관리자만 호출할 수 있습니다. 시·군·구 항목을 등록합니다. 상위 ID는 경로로 전달합니다. 같은 상위 항목 안에서 이름의 대소문자를 구분하지 않고 중복을 검사합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공. 등록된 항목 반환")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 본문 누락·잘못된 JSON·ID 형식 오류·상위 ID가 0 이하",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"입력값이 올바르지 않습니다.\"}")))
    @ApiResponse(responseCode = "401", description = "인증 정보가 없거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"인증에 실패했습니다.\"}")))
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"접근 권한이 없습니다.\"}")))
    @ApiResponse(responseCode = "404", description = "상위 항목이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"지역을 찾을 수 없습니다.\"}")))
    @ApiResponse(responseCode = "409", description = "이름 또는 코드 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"같은 상위 항목에 이미 사용 중인 지역 이름입니다.\"}")))
    ResponseEntity<SecondRegionResponse> createSecondRegion(@Positive(message = "상위 항목 ID는 양수로 입력해 주세요.") Long firstRegionId, @Valid SecondRegionCreateRequest request);

    @Operation(summary = "시·군·구 수정", description = "관리자만 호출할 수 있습니다. 시·군·구의 이름·코드·상위 항목을 수정합니다. 같은 상위 항목 안에서 이름의 대소문자를 구분하지 않고 중복을 검사합니다. 상위 ID는 요청 본문으로 전달합니다.")
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
    @ApiResponse(responseCode = "404", description = "수정 대상 또는 상위 항목이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"지역을 찾을 수 없습니다.\"}")))
    @ApiResponse(responseCode = "409", description = "이름 또는 코드 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"같은 상위 항목에 이미 사용 중인 지역 이름입니다.\"}")))
    ResponseEntity<SecondRegionResponse> updateSecondRegion(Long secondRegionId, @Valid SecondRegionRequest request);

    @Operation(summary = "시·군·구 삭제", description = "관리자만 호출할 수 있습니다. 시·군·구 항목을 물리 삭제합니다. 다른 데이터에서 참조 중이면 삭제를 거절합니다.")
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
    @ApiResponse(responseCode = "409", description = "다른 데이터에서 참조 중",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\":\"데이터 무결성 제약 조건 위반입니다.\"}")))
    ResponseEntity<SecondRegionResponse> deleteSecondRegion(Long secondRegionId);
}
