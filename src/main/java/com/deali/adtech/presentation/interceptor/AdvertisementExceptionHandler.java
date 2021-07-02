package com.deali.adtech.presentation.interceptor;

import com.deali.adtech.infrastructure.exception.*;
import com.deali.adtech.presentation.dto.ResponseEditAdvertisement;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@RestControllerAdvice
public class AdvertisementExceptionHandler {

    @ExceptionHandler(InvalidExposureDateException.class)
    public ResponseEntity handleExposureDateException(InvalidExposureDateException exception) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_EXPOSURE_DATE)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidExpiryDateException.class)
    public ResponseEntity handleExpiryDateException(InvalidExpiryDateException exception) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_EXPIRY_DATE)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ImageUploadFailureException.class)
    public ResponseEntity handleImageUploadException(ImageUploadFailureException exception) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.IMAGE_UPLOAD_FAIL)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity handleArgumentValidException(BindException exception,
                                                       BindingResult bindingResult) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_PARAMETERS,
                exception.getFieldErrors());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}
