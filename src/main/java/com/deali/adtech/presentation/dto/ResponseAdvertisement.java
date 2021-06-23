package com.deali.adtech.presentation.dto;

import com.deali.adtech.domain.AdvertisementStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseAdvertisement {
    private Long id;
    private String title;
    private Integer winningBid;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime exposureDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    private String status;
    private Long exposeCount;
    private List<String> images;

    @QueryProjection
    public ResponseAdvertisement(Long id, String title,Integer winningBid, LocalDateTime createdAt,
                                 LocalDateTime modifiedAt, LocalDateTime exposureDate,
                                 LocalDateTime expiryDate, AdvertisementStatus status, Long exposeCount) {
        this.id = id;
        this.title = title;
        this.winningBid = winningBid;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.exposureDate = exposureDate;
        this.expiryDate = expiryDate;
        this.status = status.toString();
        this.exposeCount = exposeCount;
    }

    @QueryProjection
    public ResponseAdvertisement(Long id, String title, Integer winningBid, LocalDateTime createdAt,
                                 LocalDateTime modifiedAt, LocalDateTime exposureDate,
                                 LocalDateTime expiryDate, AdvertisementStatus status) {
        this.id = id;
        this.title = title;
        this.winningBid = winningBid;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.exposureDate = exposureDate;
        this.expiryDate = expiryDate;
        this.status = status.toString();
    }
}
