package com.deali.adtech.infrastructure.util.event;

import com.deali.adtech.domain.Advertisement;
import lombok.Getter;

@Getter
public class AdvertisementRemovedEvent {
    private final Advertisement advertisement;

    public AdvertisementRemovedEvent(final Advertisement advertisement) {
        this.advertisement = advertisement;
    }
}
