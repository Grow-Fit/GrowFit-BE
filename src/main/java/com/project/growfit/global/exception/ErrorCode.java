package com.project.growfit.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "invalid input type"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "invalid type value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "method not allowed");

    private final HttpStatus status;
    private final String message;
}
