package com.deali.adtech.infrastructure.util.event.handler;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.infrastructure.util.event.AdvertisementRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//TODO:: 서비스나 기타 로직과 관련된 부가기능들은 aspect 로 도메인 로직과 관련된 흐름들은 도메인 이벤트로 처리
@RequiredArgsConstructor
@Component
public class AdvertisementDocumentHandler {
    private final MongoTemplate mongoTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void removeAdvertisementDocument(AdvertisementRemovedEvent event) {
        Advertisement advertisement = event.getAdvertisement();

        Criteria criteria = new Criteria("advertisementId")
                .is(advertisement.getId());

        Query query = new Query(criteria);

        mongoTemplate.remove(query, "advertisement");
    }
}
