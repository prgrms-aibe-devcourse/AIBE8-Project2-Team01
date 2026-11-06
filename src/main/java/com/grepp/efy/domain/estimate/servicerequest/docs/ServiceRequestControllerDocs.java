package com.grepp.efy.domain.estimate.servicerequest.docs;

import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.servicerequest.dto.*;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
            summary = "관리자 요청서 전체 목록 조회",
            description = "관리자가 모든 요청서를 페이지 단위로 조회합니다. " +
                    "취소(CLOSED)·접수 기간 만료·탈퇴 회원의 요청서를 모두 포함합니다. " +
                    "status로 상태를 필터링할 수 있으며, sortBy는 id·createdAt·requestStatus를 지원합니다. " +
                    "요청서 상세와 받은 견적은 기존 요청서 상세·요청서별 견적 목록 API로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (결과가 없으면 빈 목록)"),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지·정렬·상태 값", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 사용자", content = @Content)
    })
    ResponseEntity<PageResponse<AdminRequestResponse>> getAdminRequests(
            RequestStatus status, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    @Operation(
            summary = "고수 매칭 요청서 목록 조회",
            description = "로그인한 고수의 제공 서비스와 활동 지역에 일치하는 요청서 목록을 최신순으로 조회합니다. " +
                    "전국 활동 고수는 지역 제한 없이, 시·도 전체를 선택한 고수는 해당 시·도의 모든 시·군·구 요청서를 조회할 수 있으며, " +
                    "CLOSED 상태·본인이 작성한 요청서·탈퇴한 회원의 요청서·" +
                    "등록 후 24시간(견적 접수 기간)이 지난 요청서는 제외됩니다. " +
                    "이미 견적을 보낸 요청서는 sentQuoteId에 보낸 견적서 ID가, 보내지 않은 요청서는 null이 담깁니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 요청서 목록 조회 성공 (매칭 결과가 없으면 빈 목록 반환)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "고수 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "고수 프로필을 찾을 수 없음", content = @Content)
    })
    ResponseEntity<List<RequestMatchedResponse>> getMatchedRequests(
            @Parameter(hidden = true) CustomUserDetails userDetails);

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

    @Operation(
            summary = "서비스 요청서 취소",
            description = "로그인한 일반 회원이 본인의 요청서를 취소합니다. "
                    + "OPEN 상태에서만 취소할 수 있으며, 취소 시 요청서 상태가 CLOSED로 변경됩니다. "
                    + "의뢰 요청되지 않은(SENT) 견적서는 크레딧 예약이 해제되고 즉시 삭제됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "요청서 취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소할 수 없는 요청서 상태 (이미 CLOSED)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "USER 권한이 아니거나 요청서 작성자 본인이 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 요청서")
    })
    ResponseEntity<Void> cancelRequest(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,
            @Parameter(description = "취소할 서비스 요청서 ID", required = true, example = "1")
            Long requestId
    );
}
