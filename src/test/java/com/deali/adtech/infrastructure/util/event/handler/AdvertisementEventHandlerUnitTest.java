package com.deali.adtech.infrastructure.util.event.handler;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.util.event.AdvertisementChangedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementPostponedEvent;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementDocumentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalApplicationListenerMethodAdapter;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;

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
    @DisplayName("?????? ?????? ????????? ????????? ?????? ?????????")
    public void handle_postpone_advertisement_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement target = Advertisement.builder()
                .title("?????? ??????")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(target, "id", 1L);

        AdvertisementDocument poolTarget = documentMapper.entityToDocument(target);

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
    @DisplayName("?????? ?????? ????????? ????????? ?????? ????????? ?????? ?????? ????????? ?????? ????????? ??????")
    public void handle_advertisement_changed_success_test_not_advertising() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement target = Advertisement.builder()
                .title("?????? ??????")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(target, "id", 1L);

        AdvertisementDocument poolTarget = documentMapper.entityToDocument(target);

        mongoTemplate.save(poolTarget, "advertisement");

        target.editTitle("?????? ?????? ??????");

        /* when */
        eventPublisher.publishEvent(new AdvertisementChangedEvent(target));
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Criteria criteria = new Criteria("advertisementId").is(target.getId());
        Query findQuery = new Query().addCriteria(criteria);

        AdvertisementDocument result = mongoTemplate.findOne(findQuery, AdvertisementDocument.class);
        /* then */
        assertThat(result.getTitle()).isNotEqualTo(target.getTitle());
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? ?????? ?????????")
    public void handle_advertisement_changed_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement target = Advertisement.builder()
                .title("?????? ??????")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(target, "id", 1L);
        ReflectionTestUtils.setField(target, "status", AdvertisementStatus.ADVERTISING);

        AdvertisementDocument poolTarget = documentMapper.entityToDocument(target);
        
        mongoTemplate.save(poolTarget, "advertisement");

        target.editTitle("?????? ?????? ??????");

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