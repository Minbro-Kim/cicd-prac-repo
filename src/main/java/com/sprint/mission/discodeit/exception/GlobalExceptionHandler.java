package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 기본 에러
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        ErrorResponse response = ErrorResponse.of(500, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessLogicException e){
        ErrorResponse response = ErrorResponse.of(e.getExceptionCode().getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.valueOf(e.getExceptionCode().getCode())).body(response);
    }

    //파라미터
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.badRequest().body(ErrorResponse.of(e.getBindingResult()));
    }
    //경로
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e){
        return ResponseEntity.badRequest().body(ErrorResponse.of(e.getConstraintViolations()));
    }
}