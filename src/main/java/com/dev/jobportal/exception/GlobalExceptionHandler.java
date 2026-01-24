package com.dev.jobportal.exception;

import com.dev.jobportal.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleJobNotFoundException(JobNotFoundException e) {
        log.error("JobNotFoundException occurred");
        return ErrorResponse
                .builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .statusCode(404)
                .build();
    }

    @ExceptionHandler(ApplicantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleApplicantNotFoundException(ApplicantNotFoundException e){
        log.error("ApplicantNotFoundException occurred");
        return ErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .statusCode(404)
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e){
        log.error("UserNotFoundException occurred");
        return ErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .statusCode(404)
                .build();
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleApplicationNotFoundException(ApplicationNotFoundException e){
        log.error("ApplicationNotFoundException occurred");
        return ErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .statusCode(404)
                .build();
    }
}
