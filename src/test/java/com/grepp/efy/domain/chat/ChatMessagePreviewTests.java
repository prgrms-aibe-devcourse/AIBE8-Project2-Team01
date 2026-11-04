package com.grepp.efy.domain.chat;

import com.grepp.efy.domain.chat.chatmessage.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 채팅방 목록 미리보기(ChatMessage.preview) 단위 테스트
@SuppressWarnings("NonAsciiCharacters")
class ChatMessagePreviewTests {

    @Test
    void _50자_이하면_내용_그대로() {
        String content = "가".repeat(50);

        assertEquals(content, preview(content));
    }

    @Test
    void _50자를_넘으면_앞_50자만() {
        assertEquals("가".repeat(50), preview("가".repeat(50) + "나".repeat(450)));
    }

    @Test
    void 줄바꿈은_공백으로_바꾼다() {
        assertEquals("내일 오전 가능할까요? 주소 보내드릴게요", preview("내일 오전\n가능할까요?\r\n주소 보내드릴게요"));
    }

    @Test
    void 이모지를_반으로_자르지_않는다() {
        // 😀는 char 2개(서로게이트 쌍)지만 한 글자로 센다
        String content = "😀".repeat(60);

        String preview = preview(content);

        assertEquals("😀".repeat(50), preview);
        assertEquals(50, preview.codePointCount(0, preview.length()));
    }

    @Test
    void 이미지_메시지는_사진() {
        assertEquals("사진", ChatMessage.image(null, null, null).preview());
    }

    private static String preview(String content) {
        return ChatMessage.text(null, null, content).preview();
    }
}
