package com.deali.adtech.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    //Advertisement
    INVALID_EXPOSURE_DATE(400, "A001", "시작 일자는 현재 시간 이전일 수 없습니다."),
    INVALID_EXPIRY_DATE(400, "A002", "만료 일자는 광고 시작 일자 혹은 현재 시간 이전일 수 없습니다."),
    IMAGE_UPLOAD_FAIL(500, "A003", "이미지 업로드에 실패했습니다.")
    ;

    private final String code;
    private final String defaultMessage;
    private int status;

    ErrorCode(final int status, final String code, final  String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
