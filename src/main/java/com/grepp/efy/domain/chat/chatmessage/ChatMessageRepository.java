package com.grepp.efy.domain.chat.chatmessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // cursor가 없으면 최신부터, 있으면 그 메시지보다 오래된 것부터. 삭제된 메시지 제외
    @Query("""
        select m from ChatMessage m
        join fetch m.sender
        where m.chatRoom.id = :roomId
          and m.deletedAt is null
          and (:cursor is null or m.id < :cursor)
        order by m.id desc
        """)
    List<ChatMessage> findMessages(@Param("roomId") Long roomId, @Param("cursor") Long cursor, Pageable pageable);
}
