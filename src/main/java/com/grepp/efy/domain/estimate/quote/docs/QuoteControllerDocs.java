package com.grepp.efy.domain.estimate.quote.docs;

import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.quote.dto.*;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "견적서", description = "견적서 작성·목록·상세 조회 API")
@SecurityRequirement(name = "bearerAuth")
public interface QuoteControllerDocs {

    @Operation(
            summary = "관리자 견적서 전체 목록 조회",
            description = "관리자가 모든 견적서를 페이지 단위로 조회합니다. " +
                    "발송(SENT)·의뢰 요청(REQUESTED) 견적서를 모두 포함하며, 72시간이 지난 SENT 견적서는 스케줄러가 삭제하기 전까지 함께 조회됩니다. " +
                    "status로 상태를 필터링할 수 있으며, sortBy는 id·sentAt·price·quoteStatus를 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (결과가 없으면 빈 목록)"),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지·정렬·상태 값", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 사용자", content = @Content)
    })
    ResponseEntity<PageResponse<AdminQuoteResponse>> getAdminQuotes(
            QuoteStatus status, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    @Operation(
            summary = "견적서 상세 조회",
            description = """
                    견적서의 상세 정보를 조회한다.
                    
                    - 견적서를 발송한 고수, 견적서가 도착한 요청서의 작성자, 관리자만 조회할 수 있다.
                    - 역할(USER·EXPERT) 전환과 관계없이 소유 관계로 판단한다.
                    - 발송 후 72시간이 지난 SENT 견적서는 찾을 수 없다(404). REQUESTED 견적서는 기간과 관계없이 조회된다.
                    - 견적서를 보낸 고수 정보와 요청서 기본 정보를 함께 반환한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "견적서 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QuoteResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "견적서의 당사자 또는 관리자가 아님"),
            @ApiResponse(responseCode = "404", description = "견적서를 찾을 수 없거나 유효기간이 지남")})
    ResponseEntity<QuoteResponse> getQuote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(name = "quoteId", description = "조회할 견적서 ID", required = true, in = ParameterIn.PATH, example = "1")
            @PathVariable Long quoteId);

    @Operation(
            summary = "발송한 견적서 목록 조회",
            description = """
                    로그인한 고수가 본인이 발송한 견적서 목록을 조회한다.
                    
                    - EXPERT 권한이 필요하다.
                    - 본인이 발송한 견적서만 조회한다.
                    - SENT, REQUESTED 상태의 견적서를 조회한다.
                    - 발송 시각 최신순으로 반환한다.
                    - 견적서가 없으면 빈 배열을 반환한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "견적서 목록 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = QuoteResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "고수 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "고수 프로필을 찾을 수 없음")
    })
    ResponseEntity<List<QuoteResponse>> getQuotes(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "요청서별 견적 목록 조회",
            description = """
                    요청서에 도착한 견적서 목록을 조회한다.
                    
                    - 요청서 작성자 본인과 관리자만 조회할 수 있다.
                    - 발송 후 72시간 이내의 SENT 견적서와 REQUESTED 견적서를 조회한다.
                    - 발송 시각 최신순으로 반환한다.
                    - 견적서가 없으면 빈 배열을 반환한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청서별 견적 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = QuoteRequestResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "요청서 작성자가 아니거나 접근 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "요청서를 찾을 수 없음")})
    ResponseEntity<List<QuoteRequestResponse>> getRequestQuotes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(name = "requestId", description = "견적 목록을 조회할 요청서 ID",
                    required = true, in = ParameterIn.PATH, example = "1")
            @PathVariable Long requestId);

    @Operation(
            summary = "견적서 작성 및 발송",
            description = """
                    고수가 요청서를 확인한 후 견적서를 작성하고 발송한다.
                    
                    - EXPERT 권한이 필요하다.
                    - 요청서와 고수의 서비스·지역이 매칭되어야 한다.
                    - 본인이 작성한 요청서에는 견적을 발송할 수 없다.
                    - 동일 고수는 동일 요청서에 중복 견적을 발송할 수 없다.
                    """
    )
    @ApiResponses({@ApiResponse(responseCode = "201", description = "견적서 작성 및 발송 성공",
            content = @Content(schema = @Schema(implementation = QuoteCreateResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 요청서 상태 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "고수 권한이 없거나 견적 발송 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "요청서 또는 고수 프로필을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "동일 요청서에 이미 견적을 발송함")})
    ResponseEntity<QuoteCreateResponse> createQuote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(name = "requestId", description = "견적을 발송할 요청서 ID",
                    required = true, in = ParameterIn.PATH, example = "1")
            @PathVariable Long requestId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "견적서 작성 정보", required = true,
                    content = @Content(schema = @Schema(implementation = QuoteCreateRequest.class))
            )
            @Valid @RequestBody QuoteCreateRequest request
    );

    @Operation(
            summary = "견적서 회수 및 삭제",
            description = """
                    로그인한 고수가 발송한 견적서를 회수하고 삭제한다.
                    
                    - EXPERT 권한이 필요하다.
                    - 본인이 발송한 견적서만 삭제할 수 있다.
                    - SENT 상태의 견적서만 삭제할 수 있다. 요청서가 MATCHED여도 본인의 SENT 견적은 삭제할 수 있다.
                    - REQUESTED 상태의 견적서는 삭제할 수 없다.
                    - 발송 후 72시간이 지난 SENT 견적서는 찾을 수 없다(404).
                    - 삭제 시 예약된 2크레딧이 해제되고 견적서는 물리적으로 삭제된다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "견적서 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "고수 권한이 없거나 본인의 견적서가 아님"),
            @ApiResponse(responseCode = "404", description = "견적서를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 의뢰 요청된 견적서는 삭제할 수 없음")
    })
    ResponseEntity<Void> deleteQuote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(name = "quoteId", description = "삭제할 견적서 ID",
                    required = true, in = ParameterIn.PATH, example = "1")
            @PathVariable Long quoteId
    );
}