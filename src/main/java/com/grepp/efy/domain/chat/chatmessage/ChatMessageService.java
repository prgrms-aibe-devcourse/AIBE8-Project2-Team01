package com.grepp.efy.domain.chat.chatmessage;

import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageRequest;
import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageResponse;
import com.grepp.efy.domain.chat.chatparticipant.ChatParticipant;
import com.grepp.efy.domain.chat.chatparticipant.ChatParticipantRepository;
import com.grepp.efy.domain.chat.chatroom.ChatRoom;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    // 참여 중인 회원만 보낼 수 있다. 저장과 채팅방 마지막 메시지 갱신은 한 트랜잭션
    @Transactional
    public ChatMessageResponse send(Long roomId, Long memberId, ChatMessageRequest request) {
        // 이미지 메시지는 후속 이슈
        if (request.type() != MessageType.TEXT) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION);
        }

        // 인터셉터에서도 검사하지만, 다른 곳에서 호출될 때를 위해 한 번 더 확인
        ChatParticipant participant = chatParticipantRepository.findActiveParticipant(roomId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION));

        ChatRoom room = participant.getChatRoom();
        Member sender = participant.getMember();

        ChatMessage message = chatMessageRepository.save(
                new ChatMessage(room, sender, request.type(), request.content()));
        room.updateLastMessage(message.getContent(), message.getCreatedAt());

        return new ChatMessageResponse(
                message.getId(),
                room.getId(),
                sender.getId(),
                sender.getNickname(),
                message.getType(),
                message.getContent(),
                message.getCreatedAt());
    }
}
