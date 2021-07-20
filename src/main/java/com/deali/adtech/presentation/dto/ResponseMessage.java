package com.deali.adtech.presentation.dto;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    ADVERTISEMENT_CREATED("광고가 생성되었습니다."),
    ADVERTISEMENT_DELETE("광고가 삭제되었습니다."),
    ADVERTISEMENT_EDITED("광고가 수정되었습니다."),
    ADVERTISEMENT_PAUSED("광고가 일시정지 되었습니다."),
    ADVERTISEMENT_UNPAUSE("일시정지가 해제되었습니다.")
    ;

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
