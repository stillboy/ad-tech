package com.deali.adtech.presentation.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class ResponseCreative {
    private ObjectId _id;
    private Long advertisementId;
    private String title;
    private Integer winningBid;
    private LocalDateTime expiryDate;
    private LocalDateTime modifiedAt;
    private Double score;
    private String imagePath;
}
