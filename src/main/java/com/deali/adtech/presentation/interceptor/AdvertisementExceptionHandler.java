package com.deali.adtech.presentation.interceptor;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class AdvertisementExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ModelAndView handleAdvertisementBindingException(BindException bindException) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setStatus(HttpStatus.BAD_REQUEST);

        modelAndView.addObject("errors",
                bindException.getBindingResult().getAllErrors());

        modelAndView.setViewName("home");

        return modelAndView;
    }
}
