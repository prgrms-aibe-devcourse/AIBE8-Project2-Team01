package com.grepp.efy.domain.estimate.servicerequest.docs;

import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestCreateRequest;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestCreateResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestListResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "서비스 요청서",
        description = "서비스 요청서 작성 및 관리 API"
)
public interface ServiceRequestControllerDocs {

    @Operation(
            summary = "본인 요청서 목록 조회",
            description = "로그인한 일반 회원이 본인이 작성한 서비스 요청서 목록을 최신순으로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청서 목록 조회 성공 (요청서가 없으면 빈 배열 반환)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "일반 회원(USER) 권한이 아닌 사용자")
    })
    ResponseEntity<List<ServiceRequestListResponse>> getMyRequests(
            @Parameter(hidden = true)
            CustomUserDetails userDetails
    );

    @Operation(
            summary = "서비스 요청서 상세 조회",
            description = "로그인한 일반 회원이 본인이 작성한 서비스 요청서의 상세 정보와 질문별 답변을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청서 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "조회 권한 없음 (USER 권한이 아니거나 작성자 본인이 아닌 경우)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 요청서")
    })
    ResponseEntity<ServiceRequestResponse> getRequest(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(description = "조회할 서비스 요청서 ID", required = true, example = "1")
            Long requestId
    );

    @Operation(
            summary = "서비스 요청서 작성",
            description = "로그인한 고객이 카테고리, 지역, 질문별 답변을 제출하여 요청서를 작성합니다. 요청서와 답변은 함께 저장되며 초기 상태는 OPEN입니다."
    )
    @ApiResponse(responseCode = "201", description = "요청서 작성 성공")
    @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않거나 필수 질문 답변이 누락된 경우")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음")
    @ApiResponse(responseCode = "409", description = "동일 소분류의 요청서가 이미 존재하는 경우")
    ResponseEntity<ServiceRequestCreateResponse> createRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ServiceRequestCreateRequest request
    );
}
