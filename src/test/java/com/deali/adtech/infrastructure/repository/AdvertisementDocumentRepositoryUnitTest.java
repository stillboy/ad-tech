package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.presentation.dto.ResponseCreative;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@AutoConfigureDataMongo
@SpringBootTest
public class AdvertisementDocumentRepositoryUnitTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdvertisementDocumentRepository documentRepository;

    private int maxSize = 50;

    @BeforeEach
    public void setUp() {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        for(int i = 0; i < maxSize; ++i) {
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

    @AfterEach
    public void tearDown() {
        mongoTemplate.dropCollection("advertisement");
    }

    @Test
    @DisplayName("소재 풀 노출 성공 테스트")
    public void search_top_10_advertisement_success_test() throws Exception {
        /* given */

        /* when */
        List<ResponseCreative> results = documentRepository.searchTop10Advertisement();

        ResponseCreative maxScore = results.get(0);
        ResponseCreative minScore = results.get(results.size()-1);

        /* then */
        assertThat(results).hasSize(5);
        assertThat(maxScore.getScore())
                .isGreaterThanOrEqualTo(minScore.getScore());
    }

    @Test
    @DisplayName("소재 풀 삭제 성공 테스트")
    public void remove_advertisement_pool_success_test() throws Exception {
        /* given */
        Criteria criteria = new Criteria("advertisementId");
        criteria.is(1L);

        Query findQuery = new Query();
        findQuery.addCriteria(criteria);

        AdvertisementDocument target
                = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);

        /* when */
        documentRepository.remove(target);
        List<AdvertisementDocument> results = mongoTemplate.findAll(AdvertisementDocument.class);

        /* then */
        assertThat(results).hasSize(maxSize-1);
    }

    @Test
    @DisplayName("소재 풀 수정 성공 테스트")
    public void update_advertisement_pool_success_test() throws Exception {
        /* given */
        Criteria criteria = new Criteria("advertisementId");
        criteria.is(1L);

        Query findQuery = new Query();
        findQuery.addCriteria(criteria);

        AdvertisementDocument target
                = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);

        String updatedTitle = "수정 광고";

        ReflectionTestUtils.setField(target, "title", updatedTitle);
        /* when */
        documentRepository.update(target);
        target = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);

        /* then */
        assertThat(target)
                .hasFieldOrPropertyWithValue("title", updatedTitle);
    }

    @Test
    public void timeTest() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        System.out.println(time);
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
