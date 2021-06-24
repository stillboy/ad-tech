package com.deali.adtech.domain;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@QueryEntity
@Getter
@Document(collection = "advertisement")
public class AdvertisementDocument {
    @Id
    private ObjectId _id;
    private String title;
    private Integer winningBid;
    private LocalDateTime modifiedAt;
    private LocalDateTime expiryDate;
    private Long advertisementId;
    private String imagePath;
}
