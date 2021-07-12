package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.InvalidExpiryDateException;
import com.deali.adtech.infrastructure.exception.InvalidExposureDateException;
import com.deali.adtech.infrastructure.exception.StatusMismatchException;
import com.deali.adtech.infrastructure.util.event.AdvertisementPostponedEvent;
import com.deali.adtech.infrastructure.util.event.Events;

import java.time.LocalDateTime;

public class AdvertisingStatusStrategy implements StatusStrategy {
    @Override
    public void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate) {
        if(advertisement.getStatus() != AdvertisementStatus.ADVERTISING) {
            throw new StatusMismatchException();
        }

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime originExposureDate = advertisement.getExposureDate();

        validateExposureDate(advertisement, exposureDate);
        validateExpiryDate(advertisement, expiryDate, exposureDate);

        advertisement.changeExposureDate(exposureDate);
        advertisement.changeExpiryDate(expiryDate);

        if(exposureDate.isAfter(current)) {
            advertisement.postpone();
            Events.raise(new AdvertisementPostponedEvent(advertisement));
        }
    }

    @Override
    public void validateExposureDate(Advertisement advertisement, LocalDateTime exposureDate) {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime originExposureDate = advertisement.getExposureDate();

        if(exposureDate != null && exposureDate.isEqual(originExposureDate)) return;

        if(exposureDate == null || exposureDate.isBefore(current) || exposureDate.isEqual(current)
                || exposureDate.isBefore(originExposureDate)) {
            throw new InvalidExposureDateException();
        }
    }

    @Override
    public void validateExpiryDate(Advertisement advertisement, LocalDateTime expiryDate, LocalDateTime exposureDate) {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime originExposureDate = advertisement.getExposureDate();
        LocalDateTime originExpiryDate = advertisement.getExpiryDate();

        if(expiryDate == null || expiryDate.isBefore(current)) {
            throw new InvalidExpiryDateException();
        }

        if(expiryDate.isEqual(exposureDate) || expiryDate.isBefore(exposureDate)
                || expiryDate.isBefore(originExposureDate)
                || expiryDate.isEqual(originExposureDate)) {
            throw new InvalidExpiryDateException();
        }
    }
}
