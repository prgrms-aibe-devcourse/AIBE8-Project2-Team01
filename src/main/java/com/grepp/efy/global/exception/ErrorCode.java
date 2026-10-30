package com.grepp.efy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 서비스에서 던지는 모든 에러의 상태 코드와 메시지를 모아 둔 곳
//
// [사용 규칙]
// 1. 각자 자기 도메인 구역 안에만 추가한다 (구역이 떨어져 있어서 git 충돌이 나지 않는다)
// 2. 추가하기 전에 같은 의미의 상수가 이미 있는지 먼저 검색한다 (예: MEMBER_NOT_FOUND는 모든 도메인에서 재사용)
// 3. 던질 때는 throw new BusinessException(ErrorCode.XXX_EXCEPTION); 한 줄이면 된다
//    GlobalExceptionHandler는 수정하지 않는다
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT_EXCEPTION(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_JSON_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."),
    INVALID_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "경로 또는 쿼리 값의 타입이 올바르지 않습니다."),
    INVALID_PAGE_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "페이지 번호, 크기 또는 정렬 조건이 올바르지 않습니다."),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    PATH_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "요청한 경로를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED_EXCEPTION(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    DATA_CONFLICT_EXCEPTION(HttpStatus.CONFLICT, "데이터 무결성 제약 조건 위반입니다."),
    INTERNAL_SERVER_ERROR_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // ① 회원·인증
    MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL_EXCEPTION(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME_EXCEPTION(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    LOGIN_FAILED_EXCEPTION(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    PASSWORD_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다."),

    DUPLICATE_PHONE_EXCEPTION(HttpStatus.CONFLICT, "이미 사용 중인 전화번호입니다."),
    SELF_ROLE_CHANGE_NOT_ALLOWED_EXCEPTION(HttpStatus.BAD_REQUEST, "본인의 관리자 권한은 변경할 수 없습니다."),
    RESERVED_MEMBER_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, "탈퇴 회원용 이메일이나 닉네임은 사용할 수 없습니다."),

    // ② 기준 데이터
    CATEGORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    DUPLICATE_CATEGORY_EXCEPTION(HttpStatus.CONFLICT, "같은 상위 항목에 이미 사용 중인 카테고리 이름입니다."),
    CATEGORY_HAS_CHILDREN_EXCEPTION(HttpStatus.CONFLICT, "하위 카테고리가 있어 처리할 수 없습니다."),
    REGION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "지역을 찾을 수 없습니다."),
    DUPLICATE_REGION_EXCEPTION(HttpStatus.CONFLICT, "같은 상위 항목에 이미 사용 중인 지역 이름입니다."),
    DUPLICATE_REGION_CODE_EXCEPTION(HttpStatus.CONFLICT, "이미 사용 중인 지역 코드입니다."),
    REGION_HAS_CHILDREN_EXCEPTION(HttpStatus.CONFLICT, "하위 지역이 있어 처리할 수 없습니다."),


    // ③ 고수


    // ④ 커뮤니티


    // ⑤ 견적 요청·매칭
    QUESTION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    REQUIRED_ANSWER_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "필수 질문에 모두 답변해주세요."),
    REQUEST_ANSWER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "요청서 답변을 찾을 수 없습니다."),
    DUPLICATE_SERVICE_REQUEST_EXCEPTION(HttpStatus.CONFLICT, "해당 카테고리에 이미 유효한 요청서가 존재합니다."),
    DUPLICATE_ANSWER_EXCEPTION(HttpStatus.BAD_REQUEST, "동일한 질문에 중복 답변할 수 없습니다."),
    REQUEST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "요청서를 찾을 수 없습니다."),
    INVALID_REQUEST_STATUS_EXCEPTION(HttpStatus.BAD_REQUEST, "취소할 수 없는 상태의 요청서입니다."),

    // ⑥ 리뷰·찜·좋아요


    // ⑦ 크레딧·결제


    // ⑧ 채팅


    ;

    private final HttpStatus status;
    private final String message;
}
