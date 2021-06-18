package com.deali.adtech.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Document(collection = "advertisement")
public class AdvertisementDocument {
    @Id
    @GeneratedValue
    private ObjectId _id;
    private String title;
    private Integer winningBid;
    private LocalDateTime modifiedAt;
    private LocalDateTime expiryDate;
    private Long advertisementId;
}
