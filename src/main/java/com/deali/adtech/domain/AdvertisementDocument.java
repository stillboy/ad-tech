package com.deali.adtech.domain;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
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

    @Builder
    public AdvertisementDocument(String title, Integer winningBid, LocalDateTime modifiedAt,
                                 LocalDateTime expiryDate, Long advertisementId, String imagePath) {
        this.title = title;
        this.winningBid = winningBid;
        this.modifiedAt = modifiedAt;
        this.expiryDate = expiryDate;
        this.advertisementId = advertisementId;
        this.imagePath = imagePath;
    }
}
