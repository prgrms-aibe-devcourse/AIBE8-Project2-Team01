package com.grepp.efy.domain.chat.docs;

import com.grepp.efy.domain.chat.dto.ChatRoomCreateRequest;
import com.grepp.efy.domain.chat.dto.ChatRoomCreateResponse;
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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "채팅방", description = "채팅방 API")
public interface ChatRoomControllerDocs {

    @Operation(summary = "채팅방 생성",
            description = "견적의 고객(요청서 작성자)과 고수(견적서 작성자)를 참여자로 하는 채팅방을 만듭니다. "
                    + "해당 견적의 고객 또는 고수 본인만 생성할 수 있으며, 견적 하나당 채팅방은 하나만 만들 수 있습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = ChatRoomCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = "quoteId 누락 또는 1 미만",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 오류", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "해당 견적의 고객·고수가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "견적 당사자 아님", value = """
                            {
                              "message": "해당 견적서의 당사자가 아닙니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 견적",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "견적 없음", value = """
                            {
                              "message": "견적서를 찾을 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "409", description = "해당 견적의 채팅방이 이미 있음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "채팅방 중복", value = """
                            {
                              "message": "이미 채팅방이 존재합니다."
                            }
                            """)
            }))
    ResponseEntity<ChatRoomCreateResponse> createChatRoom(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Valid ChatRoomCreateRequest request);
}
