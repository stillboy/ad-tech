package com.deali.adtech.infrastructure.util;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AdvertisementDocumentHandler implements AdvertisementNotifier{
    private final MongoTemplate mongoTemplate;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void removeAdvertisementDocument(AdvertisementRemovedEvent event) {
        Advertisement advertisement = event.getAdvertisement();

        Criteria criteria = new Criteria("advertisementId")
                                    .is(advertisement.getId());

        Query query = new Query(criteria);

        mongoTemplate.remove(query);
    }
}
