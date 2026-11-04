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

    // 채팅방 목록 미리보기 길이 (chat_room.last_message_preview는 255자 컬럼)
    private static final int PREVIEW_LENGTH = 50;

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

    // 채팅방 목록의 마지막 메시지 미리보기. 한 줄로 바꾸고 50자까지 (이모지가 깨지지 않게 글자 단위로 자른다)
    public String preview() {
        if (type == MessageType.IMAGE) {
            return "사진";
        }
        String oneLine = content.replaceAll("\\R", " ");
        if (oneLine.codePointCount(0, oneLine.length()) <= PREVIEW_LENGTH) {
            return oneLine;
        }
        return oneLine.substring(0, oneLine.offsetByCodePoints(0, PREVIEW_LENGTH));
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
