package com.grepp.efy.domain.estimate.quote.docs;

import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "견적서", description = "견적서 작성 및 발송 API")
public interface QuoteControllerDocs {

    @Operation(
            summary = "견적서 상세 조회",
            description = """
                    로그인한 고수가 본인이 발송한 견적서의 상세 정보를 조회한다.
                    
                    - EXPERT 권한이 필요하다.
                    - 본인이 발송한 견적서만 조회할 수 있다.
                    - 견적서 상태와 요청서 기본 정보를 함께 반환한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "견적서 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QuoteResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "고수 권한이 없거나 본인의 견적서가 아님"),
            @ApiResponse(responseCode = "404", description = "견적서 또는 고수 프로필을 찾을 수 없음")
    })
    ResponseEntity<QuoteResponse> getQuote(
            @Parameter(name = "quoteId", description = "조회할 견적서 ID", required = true, example = "1")
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long quoteId);

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
}