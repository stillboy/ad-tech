package com.deali.adtech.infrastructure.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private int status;
    private String code;

    @Builder
    public ErrorResponse(final ErrorCode errorCode) {
        this.message = errorCode.getDefaultMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}
