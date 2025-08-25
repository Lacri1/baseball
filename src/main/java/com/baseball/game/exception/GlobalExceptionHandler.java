package com.baseball.game.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.baseball.game.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleGameNotFoundException(GameNotFoundException e) {
        logger.warn("게임을 찾을 수 없음: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error("게임을 찾을 수 없습니다.", "GAME_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidGameStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidGameStateException(InvalidGameStateException e) {
        logger.warn("잘못된 게임 상태: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(e.getMessage(), "INVALID_GAME_STATE");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException e) {
        logger.warn("입력값 검증 실패: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(e.getMessage(), "VALIDATION_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        logger.error("런타임 예외 발생: {}", ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error("서버 내부 오류가 발생했습니다.", "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}