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

    // 읽음 처리 기준. 메시지가 없으면 null
    @Query("select max(m.id) from ChatMessage m where m.chatRoom.id = :roomId")
    Long findLatestMessageId(@Param("roomId") Long roomId);

    // 방마다 내 읽음 위치 이후에 상대방이 보낸 메시지 수 (삭제 제외). 안 읽은 메시지가 없는 방은 결과에 없다
    @Query("""
        select p.chatRoom.id as roomId, count(m) as unreadCount
        from ChatParticipant p
        join ChatMessage m on m.chatRoom = p.chatRoom
        where p.member.id = :memberId
          and p.chatRoom.id in :roomIds
          and m.sender.id <> :memberId
          and m.deletedAt is null
          and (p.lastReadMessageId is null or m.id > p.lastReadMessageId)
        group by p.chatRoom.id
        """)
    List<UnreadCount> countUnread(@Param("memberId") Long memberId, @Param("roomIds") List<Long> roomIds);

    interface UnreadCount {
        Long getRoomId();

        Long getUnreadCount();
    }
}
