package com.deali.adtech.domain;

import com.deali.adtech.domain.service.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public enum AdvertisementStatus {
    WAITING(new WaitingStatusStrategy()),
    ADVERTISING(new AdvertisingStatusStrategy()),
    EXPIRED(new ExpiredStatusStrategy()),
    DELETED(new DeletedStatusStrategy()),
    PAUSED(new PausedStatusStrategy())
    ;

    private final StatusStrategy strategy;

    AdvertisementStatus(StatusStrategy strategy) {
        this.strategy = strategy;
    }

    public void changeDuration(Advertisement advertisement, LocalDateTime exposureDate,
                               LocalDateTime expiryDate) {
        strategy.changeDuration(advertisement, exposureDate, expiryDate);
    }
}
