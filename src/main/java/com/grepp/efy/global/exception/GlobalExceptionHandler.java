package com.grepp.efy.global.exception;

import com.grepp.efy.global.security.RefreshTokenCookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

// 모든 예외를 ErrorResponse 형식으로 바꿔서 응답한다
// 팀원은 이 파일을 수정하지 않는다. 새 에러는 ErrorCode에 한 줄 추가하면 된다.
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final RefreshTokenCookie refreshTokenCookie;

    // [커스텀] 서비스에서 던진 BusinessException
    // ErrorCode에 든 상태 코드와 메시지로 응답을 만든다. 에러가 늘어나도 이 메서드 하나가 모두 처리한다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();

        // 리프레시 토큰이 잘못되면 브라우저에 남은 쿠키도 지운다
        if (code == ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION) {
            return ResponseEntity.status(code.getStatus())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.expire().toString())
                    .body(new ErrorResponse(code.getMessage()));
        }
        return toResponse(code);
    }

    // [스프링] 400 @Valid 검증 실패
    // 요청 DTO의 @NotBlank, @Size 등을 만족하지 않으면 필드별 메시지를 errors에 담는다
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_EXCEPTION.getStatus())
                .body(new ErrorResponse(ErrorCode.INVALID_INPUT_EXCEPTION.getMessage(), errors));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        if (e.isForReturnValue()) {
            return toResponse(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION);
        }
        return toResponse(ErrorCode.INVALID_INPUT_EXCEPTION);
    }

    // [스프링] 400 요청 본문이 없거나 JSON 형식이 잘못됨
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException() {
        return toResponse(ErrorCode.INVALID_JSON_EXCEPTION);
    }

    // [스프링] 400 경로 변수나 쿼리 파라미터의 타입이 맞지 않음
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException() {
        return toResponse(ErrorCode.INVALID_TYPE_EXCEPTION);
    }

    // [스프링] 401 컨트롤러·서비스에서 인증에 실패함
    // 스프링 시큐리티의 인증 예외를 공통 인증 실패 응답으로 바꾼다
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException() {
        return toResponse(ErrorCode.UNAUTHORIZED_EXCEPTION);
    }

    // [스프링] 403 인증된 사용자가 필요한 권한을 갖고 있지 않음
    // 컨트롤러·서비스에서 발생한 권한 예외를 공통 접근 거부 응답으로 바꾼다
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException() {
        return toResponse(ErrorCode.FORBIDDEN_EXCEPTION);
    }

    // [스프링] 404 없는 경로
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException() {
        return toResponse(ErrorCode.PATH_NOT_FOUND_EXCEPTION);
    }

    // [스프링] 405 주소는 있지만 그 HTTP 메서드를 받는 API가 없음
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION.getStatus())
                .headers(e.getHeaders())
                .body(new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION.getMessage()));
    }

    // [스프링] 409 DB 유니크·외래 키 제약 위반
    // 같은 요청이 동시에 와서 서비스의 중복 검사를 둘 다 통과한 경우 등
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException() {
        return toResponse(ErrorCode.DATA_CONFLICT_EXCEPTION);
    }

    // [기타] 500 위에서 잡지 못한 예외
    // 원인은 서버 로그에만 남기고, 클라이언트에는 내부 메시지를 보여주지 않는다
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("처리되지 않은 예외", e);
        return toResponse(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION);
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode code) {
        return ResponseEntity.status(code.getStatus()).body(new ErrorResponse(code.getMessage()));
    }
}
