package com.grepp.efy.domain.chat.chatmessage;

import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageListResponse;
import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageRequest;
import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageResponse;
import com.grepp.efy.domain.chat.chatparticipant.ChatParticipant;
import com.grepp.efy.domain.chat.chatparticipant.ChatParticipantRepository;
import com.grepp.efy.domain.chat.chatroom.ChatRoom;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.file.UploadFileService;
import com.grepp.efy.domain.file.UploadFileVisibility;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private static final int PAGE_SIZE = 30;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UploadFileService uploadFileService;
    private final SupabaseStorageClient storageClient;

    // 참여 중인 회원만 보낼 수 있다. 저장과 채팅방 마지막 메시지 갱신은 한 트랜잭션
    @Transactional
    public ChatMessageResponse send(Long roomId, Long memberId, ChatMessageRequest request) {
        // 인터셉터에서도 검사하지만, 다른 곳에서 호출될 때를 위해 한 번 더 확인
        ChatParticipant participant = chatParticipantRepository.findActiveParticipant(roomId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION));

        ChatRoom room = participant.getChatRoom();
        Member sender = participant.getMember();

        ChatMessage message = chatMessageRepository.save(switch (request.type()) {
            case TEXT -> ChatMessage.text(room, sender, request.content());
            case IMAGE -> ChatMessage.image(room, sender, privateImage(request.fileId(), memberId));
        });
        room.updateLastMessage(message.preview(), message.getCreatedAt());

        return toResponse(message, room.getId());
    }

    // 참여 중인 회원만 조회할 수 있다. 없는 채팅방도 403이라 존재 여부가 드러나지 않는다
    public ChatMessageListResponse getMessages(Long roomId, Long memberId, Long cursor) {
        if (!chatParticipantRepository.existsByChatRoomIdAndMemberIdAndLeftAtIsNull(roomId, memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        // 한 개 더 조회해서 다음 페이지가 있는지 판단
        List<ChatMessage> found = chatMessageRepository.findMessages(roomId, cursor, PageRequest.of(0, PAGE_SIZE + 1));
        boolean hasNext = found.size() > PAGE_SIZE;
        List<ChatMessage> page = hasNext ? found.subList(0, PAGE_SIZE) : found;

        List<ChatMessageResponse> messages = page.stream()
                .map(message -> toResponse(message, roomId))
                .toList();
        Long nextCursor = hasNext ? page.get(page.size() - 1).getId() : null;

        return new ChatMessageListResponse(messages, nextCursor, hasNext);
    }

    private ChatMessageResponse toResponse(ChatMessage message, Long roomId) {
        Member sender = message.getSender();
        return new ChatMessageResponse(
                message.getId(),
                roomId,
                sender.getId(),
                sender.getNickname(),
                message.getType(),
                message.getContent(),
                imageUrl(message.getImage()),
                message.getCreatedAt());
    }

    // 본인이 올린, 삭제되지 않은 PRIVATE 파일만 (아니면 FILE_NOT_FOUND / FILE_FORBIDDEN / CHAT_IMAGE_NOT_PRIVATE)
    // 채팅 사진은 개인정보가 담기기 쉬워 서명 URL로만 볼 수 있게 한다
    private UploadFile privateImage(Long fileId, Long memberId) {
        UploadFile file = uploadFileService.getMyUploadFileEntity(fileId, memberId);
        if (file.getVisibility() != UploadFileVisibility.PRIVATE) {
            throw new BusinessException(ErrorCode.CHAT_IMAGE_NOT_PRIVATE_EXCEPTION);
        }
        return file;
    }

    private String imageUrl(UploadFile image) {
        return image == null ? null : storageClient.createUrl(image.getVisibility(), image.getStoragePath());
    }
}
