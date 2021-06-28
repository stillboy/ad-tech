package com.deali.adtech.presentation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestEditAdvertisement {
    private Long id;
    @NotNull(message = "광고 제목을 입력해주세요.")
    @Size(min = 2, max = 255, message = "제목은 2~255자 사이로 입력해주세요")
    private String title;
    @NotNull(message = "낙찰가를 입력해주세요.")
    @Range(min = 1, max = 10, message = "낙찰가는 1~10사이로 지정해주세요")
    private Integer winningBid;
    private MultipartFile newImage;
}
