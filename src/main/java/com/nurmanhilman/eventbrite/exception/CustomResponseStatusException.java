package com.nurmanhilman.eventbrite.exception;

import org.springframework.http.HttpStatus;

public class CustomResponseStatusException extends RuntimeException {

    private final HttpStatus status;

    public CustomResponseStatusException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
