package com.deali.adtech.presentation.dto;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    ADVERTISEMENT_CREATED("광고가 생성되었습니다.")
    ;

    private final String message;


    ResponseMessage(String message) {
        this.message = message;
    }
}
