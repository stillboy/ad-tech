package com.deali.adtech.infrastructure.util.event;

import com.deali.adtech.domain.Advertisement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdvertisementPausedEvent extends DomainEvent{
    private final Advertisement advertisement;
}
