package com.deali.adtech.domain;

import com.deali.adtech.domain.service.*;
import lombok.Getter;

@Getter
public enum AdvertisementStatus {
    WAITING(new WaitingStatusStrategy()),
    ADVERTISING(new AdvertisingStatusStrategy()),
    EXPIRED(new ExpiredStatusStrategy()),
    DELETED(new DeletedStatusStrategy());

    private final StatusStrategy strategy;

    AdvertisementStatus(StatusStrategy strategy) {
        this.strategy = strategy;
    }
}
