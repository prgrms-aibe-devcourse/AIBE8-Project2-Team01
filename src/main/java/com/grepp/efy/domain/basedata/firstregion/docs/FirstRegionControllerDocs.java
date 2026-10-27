package com.grepp.efy.domain.basedata.firstregion.docs;

import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "시·도", description = "시·도 공개 조회 및 관리자 관리")
@ApiResponse(responseCode = "400", description = "입력값 검증 또는 ID 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = "대상 또는 상위 항목 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public interface FirstRegionControllerDocs {

    @SecurityRequirements
    @Operation(summary = "시·도 목록 조회", description = "로그인 없이 ID 오름차순 목록을 조회합니다. 하위 항목이 없으면 빈 배열을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<FirstRegionResponse>> getFirstRegions();

    @Operation(summary = "시·도 등록", description = "관리자만 호출합니다. 같은 상위 항목 안의 이름은 대소문자를 구분하지 않고 중복을 검사합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @ApiResponse(responseCode = "409", description = "이름 또는 코드 중복")
    ResponseEntity<FirstRegionResponse> createFirstRegion(FirstRegionRequest request);

    @Operation(summary = "시·도 수정", description = "관리자만 호출합니다. 이름·코드을 수정합니다. 상하위 유형은 변경하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "409", description = "이름 또는 코드 중복")
    ResponseEntity<FirstRegionResponse> updateFirstRegion(Long id, FirstRegionRequest request);

    @Operation(summary = "시·도 삭제", description = "관리자만 호출합니다. 하위 항목 또는 다른 데이터의 FK 참조가 있으면 물리 삭제를 거절합니다.")
    @ApiResponse(responseCode = "200", description = "삭제한 항목 반환")
    @ApiResponse(responseCode = "409", description = "하위 항목 또는 다른 데이터에서 참조 중", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<FirstRegionResponse> deleteFirstRegion(Long id);
}
