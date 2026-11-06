package com.grepp.efy.domain.chat.chatmessage.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 메시지 타입별 필수값 검사. 오류는 content 또는 fileId 필드에 붙는다
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChatMessageRequestValidator.class)
public @interface ValidChatMessage {

    String message() default "메시지 타입에 맞는 값을 입력해 주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
