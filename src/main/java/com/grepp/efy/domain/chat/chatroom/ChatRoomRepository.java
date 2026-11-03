package com.grepp.efy.domain.chat.chatroom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByQuoteId(Long quoteId);
}
