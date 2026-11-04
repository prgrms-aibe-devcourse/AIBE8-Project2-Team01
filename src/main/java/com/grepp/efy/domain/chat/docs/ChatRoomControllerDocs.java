package com.grepp.efy.domain.chat.docs;

import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateRequest;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateResponse;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomSummaryResponse;
import com.grepp.efy.global.common.PageResponse;
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
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "채팅방", description = "채팅방 API")
public interface ChatRoomControllerDocs {

    @Operation(summary = "채팅방 생성 (의뢰 요청)",
            description = "고객이 견적서에 의뢰 요청을 하고, 고객과 고수(견적서 작성자)를 참여자로 하는 채팅방을 만듭니다. "
                    + "요청서 작성자 본인만 요청할 수 있으며 고수는 만들 수 없습니다. "
                    + "성공하면 견적서는 REQUESTED, 요청서는 MATCHED(이미 MATCHED면 유지)로 바뀝니다. "
                    + "견적서 하나당 한 번만 요청할 수 있습니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = ChatRoomCreateResponse.class)))
    @ApiResponse(responseCode = "400", description = "quoteId 누락 또는 1 미만, 직접 취소된 요청서의 견적서",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "입력값 오류", value = """
                            {
                              "message": "입력값이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "취소된 요청서", value = """
                            {
                              "message": "취소된 요청서의 견적서에는 의뢰 요청할 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "요청서 작성자가 아님 (고수 포함)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "권한 없음", value = """
                            {
                              "message": "접근 권한이 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 발송 후 72시간이 지난 견적서",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "견적 없음", value = """
                            {
                              "message": "견적서를 찾을 수 없습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "409", description = "이미 의뢰 요청된 견적서 (채팅방이 이미 있음)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "이미 의뢰 요청됨", value = """
                            {
                              "message": "이미 의뢰 요청된 견적서입니다."
                            }
                            """)
            }))
    ResponseEntity<ChatRoomCreateResponse> createChatRoom(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Valid ChatRoomCreateRequest request);

    @Operation(summary = "내 채팅방 목록 조회",
            description = "내가 참여 중인 채팅방을 20개씩 반환하며, 나간 채팅방은 제외합니다. "
                    + "마지막 메시지 시각 최신순으로 정렬하고, 메시지가 없는 방은 생성 시각을 기준으로 합니다. "
                    + "메시지가 없으면 lastMessagePreview·lastMessageAt은 null, "
                    + "상대방 프로필 사진이 없으면 partner.profileImageUrl은 null입니다. "
                    + "unreadCount는 내 읽음 위치 이후 상대방이 보낸 메시지 수입니다(내 메시지·삭제된 메시지 제외).")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "200", description = "조회 성공. 채팅방 목록과 페이지 정보 반환")
    @ApiResponse(responseCode = "400", description = "pageNumber가 정수가 아니거나 1 미만",
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
    ResponseEntity<PageResponse<ChatRoomSummaryResponse>> getMyRooms(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터, 기본값 1)") @Positive Integer pageNumber);

    @Operation(summary = "채팅방 읽음 처리",
            description = "내 읽음 위치(lastReadMessageId)를 채팅방의 가장 최신 메시지로 옮깁니다. "
                    + "채팅방에 들어갈 때, 보고 있는 중 새 메시지를 받을 때 호출합니다. "
                    + "메시지가 없으면 바뀌지 않으며, 읽음 위치는 뒤로 가지 않습니다. "
                    + "처리 후 채팅방 목록의 unreadCount는 0이 됩니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "204", description = "처리 성공")
    @ApiResponse(responseCode = "400", description = "roomId가 정수가 아니거나 1 미만",
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
    ResponseEntity<Void> readChatRoom(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Positive Long roomId);
}
