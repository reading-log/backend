package com.api.readinglog.common.exception;

import com.api.readinglog.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    // CustomException 예외 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(HttpServletRequest request, CustomException e){
        ErrorCode errorCode = e.getErrorCode();
        log.error(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(Response.error(errorCode.getStatus(), errorCode.getMessage()));
    }

    // RuntimeException 예외 핸들러
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e){
        log.error("Error occurs {}", e.toString());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

}
