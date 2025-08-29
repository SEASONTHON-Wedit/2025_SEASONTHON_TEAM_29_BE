package com.wedit.backend.common.advice;

import com.wedit.backend.common.exception.BaseException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    // 커스텀 예외 처리 (ex. BAD_REQUEST, CONFLICT 등)
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse> handleCostumeException(BaseException ex) {

        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.fail(ex.getStatusCode(), ex.getMessage()));
    }

    // 글로벌 예외 처리 (ex. 명시되어 있지 않은 모든 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {

        log.error("[handleGlobalException] 알 수 없는 오류 발생 : {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiResponse.fail(ErrorStatus.INTERNAL_SERVER_ERROR.getStatusCode(),
                        ErrorStatus.INTERNAL_SERVER_ERROR.getMessage()));
    }

    // 필수 요청 파라미터 누락 (ex. 리뷰 ID 없이 리뷰 조회)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiResponse.fail(ErrorStatus.BAD_REQUEST_MISSING_REQUIRED_FIELD.getStatusCode(),
                        ErrorStatus.BAD_REQUEST_MISSING_REQUIRED_FIELD.getMessage() + ": " + ex.getParameterName()));
    }

    // 잘못된 인자 전달 (ex. 숫자 필드에 문자열 입력)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiResponse.fail(ErrorStatus.BAD_REQUEST_MISSING_PARAM.getStatusCode(), ex.getMessage()));
    }

    // DTO 유효성 검증 실패 (ex. @NotBlank 필드를 비운 채로 요청)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(),
                        error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiResponse.fail(ErrorStatus.BAD_REQUEST_VALID_FAILED.getStatusCode(), errorMsg));
    }

    // DB에 존재하지 않는 리소스 접근/조작 시도 (ex. 존재하지 않은 리뷰 접근)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(ApiResponse.fail(ErrorStatus.NOT_FOUND_RESOURCE.getStatusCode(),
                        ErrorStatus.NOT_FOUND_RESOURCE.getMessage()));
    }

    // 지원하지 않는 Content-Type (ex. JSON만 지원하는데 XML로 요청)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {

        String supportedType = ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String errorMsg = ex.getMessage() + " " + supportedType + " (" + ex.getMessage() + ")";

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(ApiResponse.fail(ErrorStatus.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), errorMsg));
    }
}
