package com.deali.adtech.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class ResponseCreative {
    private ObjectId _id;
    private Long advertisementId;
    private String title;
    private Integer winningBid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime expiryDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    private Double score;
    private String imagePath;
}
