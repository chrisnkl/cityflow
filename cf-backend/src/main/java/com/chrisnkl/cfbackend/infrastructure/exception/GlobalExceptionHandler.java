package com.chrisnkl.cfbackend.infrastructure.exception;

import com.chrisnkl.cfbackend.domain.exception.BackendException;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(HttpBackendException.class)
    public ResponseEntity<ProblemDetail> catchHttpBackendExceptions(HttpBackendException e) {

        HttpStatus status = HttpStatus.valueOf(e.getStatus());

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail(e.getMessage());

        return ResponseEntity.status(status).body(problemDetail);
    }


    @ExceptionHandler(BackendException.class)
    public ResponseEntity<ProblemDetail> catchBackendExceptions(BackendException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(e.getMessage());

        return ResponseEntity.internalServerError().body(problemDetail);
    }

    @ExceptionHandler(io.github.resilience4j.ratelimiter.RequestNotPermitted.class)
    public ResponseEntity<ProblemDetail> catchRateLimitException(io.github.resilience4j.ratelimiter.RequestNotPermitted e) {

        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail("Too many requests. Rate limit exceeded. Please try again later.");

        return ResponseEntity.status(status).body(problemDetail);
    }


}
