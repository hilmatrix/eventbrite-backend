package com.nurmanhilman.eventbrite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CustomResponseStatusException extends RuntimeException {

    private final HttpStatus status;

    public CustomResponseStatusException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ResponseEntity<Map<String, Object>> generateResponse() {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", this.getStatus().value());
        body.put("error", this.getStatus().getReasonPhrase());
        body.put("message", this.getMessage());
        return new ResponseEntity<>(body, this.getStatus());
    }
}
