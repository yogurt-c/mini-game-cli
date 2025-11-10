package io.yogurt.cli_mini_game.exception;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.yogurt.cli_mini_game.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());

        String errorMessage = "잘못된 요청 형식입니다.";

        // Jackson ValueInstantiationException에서 실제 원인 추출
        if (e.getCause() instanceof ValueInstantiationException vie) {
            Throwable cause = vie.getCause();
            if (cause instanceof IllegalArgumentException iae) {
                errorMessage = iae.getMessage();
            }
        }

        ApiResponse<Void> response = new ApiResponse<>(
            errorMessage,
            null,
            "INVALID_ARGUMENT"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        ApiResponse<Void> response = new ApiResponse<>(
            e.getMessage(),
            null,
            "INVALID_ARGUMENT"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.warn("CustomException: {}", e.getMessage());

        ApiResponse<Void> response = new ApiResponse<>(
            e.getMessage(),
            null,
            e.getErrorCode()
        );

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected exception occurred", e);

        ApiResponse<Void> response = new ApiResponse<>(
            "서버 내부 오류가 발생했습니다.",
            null,
            "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
