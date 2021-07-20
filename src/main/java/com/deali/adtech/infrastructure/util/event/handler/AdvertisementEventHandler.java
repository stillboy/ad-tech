package com.deali.adtech.infrastructure.util.event.handler;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.repository.AdvertisementDocumentRepository;
import com.deali.adtech.infrastructure.util.event.AdvertisementChangedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementPausedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementPostponedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementUnPausedEvent;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementDocumentMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AdvertisementEventHandler {
    private final AdvertisementDocumentRepository repository;
    private final AdvertisementDocumentMapper documentMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdvertisementPostponedEvent(AdvertisementPostponedEvent event) {
        Advertisement target = event.getAdvertisement();

        AdvertisementDocument document = documentMapper.entityToDocument(target);

        repository.remove(document);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdvertisementChangedEvent(AdvertisementChangedEvent event) {
        Advertisement target = event.getAdvertisement();

        if(target.getStatus() != AdvertisementStatus.ADVERTISING) return ;

        AdvertisementDocument document = documentMapper.entityToDocument(target);

        repository.update(document);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdvertisementPausedEvent(AdvertisementPausedEvent event) {
        Advertisement target = event.getAdvertisement();

        AdvertisementDocument document = documentMapper.entityToDocument(target);

        repository.remove(document);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdvertisementUnPausedEvent(AdvertisementUnPausedEvent event) {
        Advertisement target = event.getAdvertisement();

        AdvertisementDocument document = documentMapper.entityToDocument(target);

        repository.insert(document);
    }
}
