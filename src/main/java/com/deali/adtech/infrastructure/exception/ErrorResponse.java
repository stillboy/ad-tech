package com.deali.adtech.infrastructure.exception;

import lombok.*;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private int status;
    private String code;
    private List<FieldError> fieldErrors;

    @Builder
    public ErrorResponse(final ErrorCode errorCode) {
        this.message = errorCode.getDefaultMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.fieldErrors = new ArrayList<>();
    }

    private ErrorResponse(final ErrorCode errorCode, final List<FieldError> fieldErrors) {
        this.message = errorCode.getDefaultMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.fieldErrors = fieldErrors;
    }

    public static ErrorResponse of(final ErrorCode errorCode, final List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode, fieldErrors);
    }
}
