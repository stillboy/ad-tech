package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Random;

@AutoConfigureDataMongo
@SpringBootTest
public class AdvertisementDocumentRepositoryUnitTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdvertisementDocumentRepository documentRepository;

    @BeforeEach
    public void setUp() {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        for(int i = 0; i < 50; ++i) {
            AdvertisementDocument document = AdvertisementDocument.builder()
                    .title(randomString())
                    .winningBid(randInteger())
                    .expiryDate(expiryDate)
                    .modifiedAt(LocalDateTime.now())
                    .advertisementId((long)(i+1))
                    .imagePath("temp2.jpg")
                    .build();

            mongoTemplate.save(document, "advertisement");
        }
    }

    @Test
    @DisplayName("소재 풀 노출 성공 테스트")
    public void search_top_10_advertisement_success_test() throws Exception {
        /* given */

        /* when */


        /* then */
    }

    private String randomString() {
        Random random = new Random();
        return  random.ints(48, 122+1)
                .filter(i -> (i <= 57 || i >= 65) && (i<=90 || i>=97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private int randInteger() {
        return new Random().nextInt(10)+1;
    }
}
