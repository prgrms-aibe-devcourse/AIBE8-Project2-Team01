package com.grepp.efy.domain.chat.chatmessage.dto;

import com.grepp.efy.domain.chat.chatmessage.MessageType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChatMessageRequestValidator implements ConstraintValidator<ValidChatMessage, ChatMessageRequest> {

    @Override
    public boolean isValid(ChatMessageRequest request, ConstraintValidatorContext context) {
        // type 누락은 @NotNull이 처리
        if (request == null || request.type() == null) {
            return true;
        }
        if (request.type() == MessageType.TEXT && (request.content() == null || request.content().isBlank())) {
            return reject(context, "content", "내용을 입력해 주세요.");
        }
        if (request.type() == MessageType.IMAGE && request.fileId() == null) {
            return reject(context, "fileId", "이미지 파일을 선택해 주세요.");
        }
        return true;
    }

    private static boolean reject(ConstraintValidatorContext context, String field, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
        return false;
    }
}
