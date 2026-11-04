package com.grepp.efy.domain.chat.chatroom;

import com.grepp.efy.domain.chat.chatparticipant.ChatParticipant;
import com.grepp.efy.domain.chat.chatparticipant.ChatParticipantRepository;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateRequest;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateResponse;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomSummaryResponse;
import com.grepp.efy.domain.estimate.quote.Quote;
import com.grepp.efy.domain.estimate.quote.QuoteService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import com.grepp.efy.domain.file.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private static final int PAGE_SIZE = 20;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final QuoteService quoteService;
    private final SupabaseStorageClient storageClient;

    @Transactional
    public ChatRoomCreateResponse createChatRoom(Long memberId, ChatRoomCreateRequest request) {
        // 의뢰 요청과 함께 채팅방을 만든다. 요청서 작성자 확인·중복 요청(409)·상태 전이는 requestQuote가 처리
        Quote quote = quoteService.requestQuote(memberId, request.quoteId());

        Member customer = quote.getServiceRequest().getMember();
        Member expert = quote.getExpertProfile().getMember();

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

    // STOMP 구독 권한 검사용. 없는 채팅방도 false라 존재 여부가 드러나지 않는다
    public boolean isActiveParticipant(Long roomId, Long memberId) {
        return chatParticipantRepository.existsByChatRoomIdAndMemberIdAndLeftAtIsNull(roomId, memberId);
    }


    public PageResponse<ChatRoomSummaryResponse> getMyRooms(Long memberId, Integer pageNumber) {
        // ① 내 참여 기록 + 채팅방 (정렬은 쿼리에 있음)
        Page<ChatParticipant> myRooms = chatParticipantRepository.findMyRooms(
                memberId, PageRequest.of(pageNumber - 1, PAGE_SIZE));

        List<Long> roomIds = myRooms.getContent().stream()
                .map(p -> p.getChatRoom().getId())
                .toList();

        // ② 상대방을 방 개수와 상관없이 쿼리 한 번으로 조회 (roomId → 상대방)
        Map<Long, Member> partners = roomIds.isEmpty() ? Map.of()
                : chatParticipantRepository.findPartners(roomIds, memberId).stream()
                .collect(Collectors.toMap(p -> p.getChatRoom().getId(), ChatParticipant::getMember));

        // ③ 합쳐서 DTO로 변환
        List<ChatRoomSummaryResponse> content = myRooms.getContent().stream()
                .map(p -> toSummary(p.getChatRoom(), partners.get(p.getChatRoom().getId())))
                .toList();

        return PageResponse.of(myRooms, content);
    }

    private ChatRoomSummaryResponse toSummary(ChatRoom room, Member partner) {
        return new ChatRoomSummaryResponse(
                room.getId(),
                room.getQuote().getId(),
                partner == null ? null : toPartner(partner),
                room.getLastMessagePreview(),
                room.getLastMessageAt(),
                room.getCreatedAt());
    }

    private ChatRoomSummaryResponse.Partner toPartner(Member member) {
        UploadFile image = member.getImage();
        return new ChatRoomSummaryResponse.Partner(
                member.getId(),
                member.getNickname(),
                image == null ? null : storageClient.createUrl(image.getVisibility(), image.getStoragePath()));
    }
}
