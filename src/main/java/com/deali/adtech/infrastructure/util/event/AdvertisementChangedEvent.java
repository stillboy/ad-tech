package com.deali.adtech.infrastructure.util.event;

import com.deali.adtech.domain.Advertisement;
import lombok.Getter;

@Getter
public class AdvertisementChangedEvent extends DomainEvent {
    private final Advertisement advertisement;

    public AdvertisementChangedEvent(final Advertisement advertisement) {
        this.advertisement = advertisement;
    }

}
