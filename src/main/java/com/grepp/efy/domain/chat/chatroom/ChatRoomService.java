package com.grepp.efy.domain.chat.chatroom;

import com.grepp.efy.domain.chat.chatparticipant.ChatParticipant;
import com.grepp.efy.domain.chat.chatparticipant.ChatParticipantRepository;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateRequest;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateResponse;
import com.grepp.efy.domain.estimate.quote.Quote;
import com.grepp.efy.domain.estimate.quote.QuoteService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final QuoteService quoteService;

    @Transactional
    public ChatRoomCreateResponse createChatRoom(Long memberId, ChatRoomCreateRequest request) {
        Quote quote = quoteService.getQuoteEntity(request.quoteId());

        Member customer = quote.getServiceRequest().getMember();
        Member expert = quote.getExpertProfile().getMember();

        boolean isCustomer = customer.getId().equals(memberId);
        boolean isExpert = expert.getId().equals(memberId);

        if (!isCustomer && !isExpert) {
            throw new BusinessException(ErrorCode.NOT_QUOTE_PARTY_EXCEPTION);
        }

        if (chatRoomRepository.existsByQuoteId(quote.getId())) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS_EXCEPTION);
        }

        ChatRoom room;
        try {
            room = chatRoomRepository.saveAndFlush(new ChatRoom(quote));
        } catch (DataIntegrityViolationException e) {
            // chat_room.quote_id unique 제약 위반은 이미 채팅방이 있는 것으로 처리
            throw new BusinessException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS_EXCEPTION);
        }
        chatParticipantRepository.save(new ChatParticipant(room, customer));
        chatParticipantRepository.save(new ChatParticipant(room, expert));

        return new ChatRoomCreateResponse(room.getId(), quote.getId(), room.getCreatedAt());
    }
}
