package com.grepp.efy.domain.chat.chatparticipant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    // 채팅방에 참여 중(나가지 않음)인지 확인
    boolean existsByChatRoomIdAndMemberIdAndLeftAtIsNull(Long chatRoomId, Long memberId);

    // 내가 나가지 않은 방. 메시지가 없는 방은 생성 시각으로 정렬
    @Query(value = """
        select p from ChatParticipant p
        join fetch p.chatRoom r
        where p.member.id = :memberId and p.leftAt is null
        order by coalesce(r.lastMessageAt, r.createdAt) desc, r.id desc
        """,
            countQuery = """
        select count(p) from ChatParticipant p
        where p.member.id = :memberId and p.leftAt is null
        """)
    Page<ChatParticipant> findMyRooms(@Param("memberId") Long memberId, Pageable pageable);

    // 방마다 나를 뺀 참여자(상대방)와 프로필 사진을 한 번에 조회
    @Query("""
        select p from ChatParticipant p
        join fetch p.member m
        left join fetch m.image
        where p.chatRoom.id in :roomIds and p.member.id <> :memberId
        """)
    List<ChatParticipant> findPartners(@Param("roomIds") List<Long> roomIds, @Param("memberId") Long memberId);
}
