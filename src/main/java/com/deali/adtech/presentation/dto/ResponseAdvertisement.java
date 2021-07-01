package com.deali.adtech.presentation.dto;

import com.deali.adtech.domain.AdvertisementStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAdvertisement {
    private Long id;
    private String title;
    private Integer winningBid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime exposureDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
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
