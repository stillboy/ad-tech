package com.deali.adtech.infrastructure.util.event;

import com.deali.adtech.domain.Advertisement;
import lombok.Getter;

@Getter
public class AdvertisementPostponedEvent extends DomainEvent {
    private final Advertisement advertisement;

    public AdvertisementPostponedEvent(final Advertisement advertisement) {
        this.advertisement = advertisement;
    }
}
