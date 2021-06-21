package com.deali.adtech.presentation.dto;

import com.deali.adtech.domain.AdvertisementImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

//TODO:: 광고 생성일, 광고 개시 시작일 차이를 둘 것인가
@Data
public class RequestCreateAdvertisement {
    @NotNull(message = "광고 제목을 입력해주세요.")
    @Size(min = 2, max = 255, message = "제목은 2~255자 사이로 입력해주세요")
    private String title;

    @NotNull(message = "낙찰가를 입력해주세요.")
    @Range(min = 1, max = 10, message = "낙찰가는 1~10사이로 지정해주세요")
    private Integer winningBid;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime exposureDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime expiryDate;

    private MultipartFile image;
}
