package com.deali.adtech.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    //Advertisement
    INVALID_EXPOSURE_DATE(400, "A001", "시작 일자는 현재 시간 이전이거나 같을 수 없습니다."),
    INVALID_EXPIRY_DATE(400, "A002", "만료 일자는 광고 시작 일자 혹은 현재 시간 이전일 수 없습니다."),
    IMAGE_UPLOAD_FAIL(500, "A003", "이미지 업로드에 실패했습니다."),
    INVALID_PARAMETERS(400, "A004", "입력값이 잘못되었습니다. 다시 입력해주세요."),
    NO_SUCH_ADVERTISEMENT(404, "A005", "요청하신 광고가 존재하지 않습니다."),
    INVALID_ADVERTISING_DURATION(400, "A006", "잘못된 형식의 광고 시작/만료 기간입니다."),
    ALREADY_REMOVED(400, "A007", "이미 삭제 처리된 광고입니다."),
    FILE_SIZE_EXCEED(400, "A008", "파일 용량은 최대 30MB 입니다."),
    INVALID_FILE_TYPE(400, "A009", "이미지 확장자는 jpg, jpeg, png, gif 만 허용됩니다."),
    STATUS_MISMATCH(500, "A010", "광고의 현재 상태와 맞지 않는 요청입니다."),
    ETC(500, "A011", "서버 내부 오류 입니다."),
    INVALID_TITLE(400, "A012", "광고 제목은 2~255자 사이의 한글, 영문, 숫자 조합 입니다."),
    INVALID_WINNING_BID(400, "A013", "낙찰가는 1~10사이 입니다.")
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
