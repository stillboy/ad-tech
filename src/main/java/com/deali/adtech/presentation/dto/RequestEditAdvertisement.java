package com.deali.adtech.presentation.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RequestEditAdvertisement {
    private Long id;
    private String title;
    private Integer winningBid;
    private MultipartFile newImage;
}
