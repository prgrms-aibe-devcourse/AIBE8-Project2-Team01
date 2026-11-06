package com.grepp.efy.domain.chat.chatroom;

import com.grepp.efy.domain.estimate.quote.Quote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quote_id", nullable = false, unique = true)
    private Quote quote;

    private String lastMessagePreview;

    private LocalDateTime lastMessageAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatRoom(Quote quote) {
        this.quote = quote;
    }

    // 목록에서 마지막 메시지를 보여주려고 메시지를 저장할 때마다 갱신한다
    public void updateLastMessage(String preview, LocalDateTime at) {
        this.lastMessagePreview = preview;
        this.lastMessageAt = at;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
