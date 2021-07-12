package com.deali.adtech.infrastructure.exception;

import lombok.*;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private int status;
    private String code;
    private List<CustomFieldError> fieldErrors;

    @Builder
    public ErrorResponse(final ErrorCode errorCode) {
        this.message = errorCode.getDefaultMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.fieldErrors = new ArrayList<>();
    }

    private ErrorResponse(final ErrorCode errorCode, final List<CustomFieldError> fieldErrors) {
        this.message = errorCode.getDefaultMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.fieldErrors = fieldErrors;
    }

    public static ErrorResponse of(final ErrorCode errorCode, final List<FieldError> fieldErrors) {
        List<CustomFieldError> errors = fieldErrors.stream()
                .map(CustomFieldError::of)
                .collect(Collectors.toList());

        return new ErrorResponse(errorCode, errors);
    }

    public static ErrorResponse of(final ErrorCode errorCode, final BindingResult bindingResult) {
        return ErrorResponse.of(errorCode, bindingResult.getFieldErrors());
    }

    public static ErrorResponse ofCustomFields(final ErrorCode errorCode, final List<CustomFieldError> customFieldErrors) {
        return new ErrorResponse(errorCode, customFieldErrors);
    }

    @Getter
    @NoArgsConstructor(access=AccessLevel.PROTECTED)
    public static class CustomFieldError {
        private String field;
        private String defaultMessage;
        private String rejectedValue;

        private CustomFieldError(String field, String defaultMessage, String rejectedValue) {
            this.field = field;
            this.defaultMessage = defaultMessage;
            this.rejectedValue = rejectedValue;
        }

        public static CustomFieldError of(FieldError fieldError) {
            String rejectedValue = fieldError.getRejectedValue()==null?"":fieldError.getRejectedValue().toString();

            return new CustomFieldError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage(),
                    rejectedValue
            );
        }

        public static CustomFieldError of(FieldError fieldError, String customMessage) {
            String rejectedValue = fieldError.getRejectedValue()==null?"":fieldError.getRejectedValue().toString();

            return new CustomFieldError(
                    fieldError.getField(),
                    customMessage,
                    rejectedValue
            );
        }
    }
}
