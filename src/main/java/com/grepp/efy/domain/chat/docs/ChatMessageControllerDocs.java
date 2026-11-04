package com.grepp.efy.domain.chat.docs;

import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageListResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "채팅 메시지", description = "채팅 메시지 API (전송은 STOMP: SEND /pub/chat-rooms/{roomId}/messages)")
public interface ChatMessageControllerDocs {

    @Operation(summary = "채팅 메시지 목록 조회",
            description = "참여 중인 채팅방의 메시지를 최신순(messageId 내림차순)으로 30개씩 반환합니다. "
                    + "cursor가 없으면 가장 최신 메시지부터, 있으면 그 messageId보다 오래된 메시지부터 반환합니다. "
                    + "다음 요청에는 응답의 nextCursor를 cursor로 넣으며, 더 없으면 nextCursor는 null, hasNext는 false입니다. "
                    + "조회 중 새 메시지가 추가되어도 이어서 조회한 결과가 밀리지 않습니다. 삭제된 메시지는 제외합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ChatMessageListResponse.class)))
    @ApiResponse(responseCode = "400", description = "roomId·cursor가 정수가 아니거나 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "정수가 아님", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "1 미만", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "참여하지 않았거나 나간 채팅방, 존재하지 않는 채팅방",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 없음", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    ResponseEntity<ChatMessageListResponse> getMessages(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long roomId,
            @Parameter(description = "이 messageId보다 오래된 메시지부터 조회 (첫 요청은 생략)") @Positive Long cursor);
}
