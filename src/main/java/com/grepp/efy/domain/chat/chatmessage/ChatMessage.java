package com.grepp.efy.domain.chat.chatmessage;

import com.grepp.efy.domain.chat.chatroom.ChatRoom;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MessageType type;

    @Column(length = 500)
    private String content; // TEXT 메시지 내용. IMAGE면 null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private UploadFile image; // IMAGE 메시지의 파일. TEXT면 null

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    public static ChatMessage text(ChatRoom chatRoom, Member sender, String content) {
        return new ChatMessage(chatRoom, sender, MessageType.TEXT, content, null);
    }

    public static ChatMessage image(ChatRoom chatRoom, Member sender, UploadFile image) {
        return new ChatMessage(chatRoom, sender, MessageType.IMAGE, null, image);
    }

    private ChatMessage(ChatRoom chatRoom, Member sender, MessageType type, String content, UploadFile image) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.type = type;
        this.content = content;
        this.image = image;
    }

    // 채팅방 목록의 마지막 메시지 미리보기
    public String preview() {
        return type == MessageType.IMAGE ? "사진" : content;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
