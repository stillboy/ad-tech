package com.deali.adtech.infrastructure.util.event.handler;

import com.deali.adtech.infrastructure.util.event.AdvertisementPostponedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AdvertisementEventHandler {
    private final MongoTemplate mongoTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AdvertisementPostponedEvent event) {
        System.out.println(">>>>>>>>>>>>>>>" + event.getAdvertisement().getId());
    }
}
