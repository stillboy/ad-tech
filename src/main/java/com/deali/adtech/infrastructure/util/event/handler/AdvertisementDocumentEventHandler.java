package com.deali.adtech.infrastructure.util.event.handler;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.infrastructure.repository.AdvertisementDocumentRepository;
import com.deali.adtech.infrastructure.util.event.AdvertisementRemovedEvent;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AdvertisementDocumentEventHandler {
    private final AdvertisementDocumentRepository repository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdvertisementRemovedEvent(AdvertisementRemovedEvent event) {

        Advertisement advertisement = event.getAdvertisement();
        AdvertisementDocument document = AdvertisementDocumentMapper.INSTANCE
                .entityToDocument(advertisement);

        repository.remove(document);
    }
}
