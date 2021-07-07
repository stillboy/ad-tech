package com.deali.adtech.infrastructure.util.event.handler;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.util.event.AdvertisementChangedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementPostponedEvent;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementDocumentMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class AdvertisementEventHandlerUnitTest {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdvertisementDocumentMapper documentMapper;

    @Test
    @DisplayName("광고 연기 이벤트 핸들링 성공 테스트")
    public void handle_postpone_advertisement_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement target = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(target, "id", 1L);

        AdvertisementDocument poolTarget = documentMapper.entityToDocument(target);

        mongoTemplate.createCollection("advertisement");
        mongoTemplate.save(poolTarget, "advertisement");
        /* when */
        eventPublisher.publishEvent(new AdvertisementPostponedEvent(target));
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Criteria criteria = new Criteria("advertisementId").is(target.getId());
        Query findQuery = new Query().addCriteria(criteria);


        AdvertisementDocument result = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);
        /* then */

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("광고 변경 이벤트 핸들링 성공 테스트 현재 광고 중이지 않은 광고일 경우")
    public void handle_advertisement_changed_success_test_not_advertising() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement target = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(target, "id", 1L);

        AdvertisementDocument poolTarget = documentMapper.entityToDocument(target);

        mongoTemplate.createCollection("advertisement");
        mongoTemplate.save(poolTarget, "advertisement");

        target.editTitle("광고 제목 변경");

        /* when */
        eventPublisher.publishEvent(new AdvertisementChangedEvent(target));
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Criteria criteria = new Criteria("advertisementId").is(target.getId());
        Query findQuery = new Query().addCriteria(criteria);

        AdvertisementDocument result = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);
        /* then */
        assertThat(result)
                .hasFieldOrPropertyWithValue("title", target.getTitle());
    }

    @Test
    @DisplayName("광고 변경 이벤트 핸들링 성공 테스트")
    public void handle_advertisement_changed_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement target = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(target, "id", 1L);
        ReflectionTestUtils.setField(target, "status", AdvertisementStatus.ADVERTISING);

        AdvertisementDocument poolTarget = documentMapper.entityToDocument(target);

        mongoTemplate.createCollection("advertisement");
        mongoTemplate.save(poolTarget, "advertisement");

        target.editTitle("광고 제목 변경");

        /* when */
        eventPublisher.publishEvent(new AdvertisementChangedEvent(target));
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Criteria criteria = new Criteria("advertisementId").is(target.getId());
        Query findQuery = new Query().addCriteria(criteria);

        AdvertisementDocument result = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);
        /* then */
        assertThat(result)
                .hasFieldOrPropertyWithValue("title", target.getTitle());
    }
}