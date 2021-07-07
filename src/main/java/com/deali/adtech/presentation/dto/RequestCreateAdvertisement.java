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

@Data
public class RequestCreateAdvertisement {
    @NotNull(message = "광고 제목을 입력해주세요.")
    @Size(min = 2, max = 255, message = "제목은 2~255자 사이로 입력해주세요")
    private String title;

    @NotNull(message = "낙찰가를 입력해주세요.")
    @Range(min = 1, max = 10, message = "낙찰가는 1~10사이로 지정해주세요")
    private Integer winningBid;

    @NotNull(message = "광고 시작 기간을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime exposureDate;

    @NotNull(message = "광고 만료 기간을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiryDate;

    private MultipartFile image;
}
