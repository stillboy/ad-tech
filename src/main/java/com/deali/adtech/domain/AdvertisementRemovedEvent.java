package com.deali.adtech.domain;

import lombok.Getter;

@Getter
public class AdvertisementRemovedEvent {
    private final Advertisement advertisement;

    public AdvertisementRemovedEvent(final Advertisement advertisement) {
        this.advertisement = advertisement;
    }
}
