package com.deali.adtech.presentation.dto;

import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.domain.ExposeCount;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestEditAdvertisement {
    private Long id;
    private String title;
    private Integer winningBid;
    private MultipartFile newImage;
}
