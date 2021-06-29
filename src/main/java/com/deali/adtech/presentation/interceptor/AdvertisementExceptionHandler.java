package com.deali.adtech.presentation.interceptor;

import com.deali.adtech.infrastructure.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@ControllerAdvice
public class AdvertisementExceptionHandler {

    @ExceptionHandler(InvalidExposureDateException.class)
    @ResponseBody
    public ResponseEntity handleExposureDateException(InvalidExposureDateException exception) throws JsonProcessingException {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_EXPOSURE_DATE)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidExpiryDateException.class)
    public ModelAndView handleExpiryDateException(InvalidExpiryDateException exception) {
        ModelAndView modelAndView = createModelAndViewWithErrorCode(ErrorCode.INVALID_EXPIRY_DATE,
                "home");

        return modelAndView;
    }

    @ExceptionHandler(ImageUploadFailureException.class)
    public ModelAndView handleImageUploadException(ImageUploadFailureException exception) {
        ModelAndView modelAndView = createModelAndViewWithErrorCode(ErrorCode.IMAGE_UPLOAD_FAIL,
                "home");

        return modelAndView;
    }

    @ExceptionHandler(BindException.class)
    public ModelAndView handleAdvertisementBindingException(BindException bindException) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setStatus(HttpStatus.BAD_REQUEST);

        modelAndView.addObject("errors",
                bindException.getBindingResult().getAllErrors());

        modelAndView.setViewName("home");

        return modelAndView;
    }

    private ModelAndView createModelAndViewWithErrorCode(ErrorCode errorCode,
                                                         String viewName) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setStatus(HttpStatus.valueOf(errorCode.getStatus()));
        modelAndView.addObject("errors",
                Arrays.asList(errorCode));
        modelAndView.setViewName(viewName);

        return modelAndView;
    }
}
