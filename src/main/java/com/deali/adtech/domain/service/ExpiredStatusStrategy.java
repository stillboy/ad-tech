package com.deali.adtech.domain.service;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.exception.InvalidExpiryDateException;
import com.deali.adtech.infrastructure.exception.InvalidExposureDateException;
import com.deali.adtech.infrastructure.exception.StatusMismatchException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExpiredStatusStrategy implements StatusStrategy {

    @Override
    public void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate) {
        if(advertisement.getStatus() != AdvertisementStatus.EXPIRED) {
            throw new StatusMismatchException();
        }

        validateExposureDate(advertisement, exposureDate);
        validateExpiryDate(advertisement, expiryDate, exposureDate);

        advertisement.changeExposureDate(exposureDate);
        advertisement.changeExpiryDate(expiryDate);
        advertisement.updateExpiredDuration();
    }

    @Override
    public void validateExposureDate(Advertisement advertisement, LocalDateTime exposureDate) {
        LocalDateTime current = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime originExposureDate = advertisement.getExposureDate();

        if(exposureDate != null && exposureDate.isEqual(originExposureDate)) return;

        if(exposureDate == null
                || exposureDate.isBefore(current)
                || !exposureDate.isAfter(originExposureDate)) {
            throw new InvalidExposureDateException();
        }
    }

    @Override
    public void validateExpiryDate(Advertisement advertisement, LocalDateTime expiryDate, LocalDateTime exposureDate) {
        LocalDateTime current = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime originExposureDate = advertisement.getExposureDate();
        LocalDateTime originExpiryDate = advertisement.getExpiryDate();

        if(expiryDate == null
                || !expiryDate.isAfter(current)
                || expiryDate.isEqual(originExpiryDate)) {
            throw new InvalidExpiryDateException();
        }

        if(!expiryDate.isAfter(originExpiryDate)) {
            throw new InvalidExpiryDateException();
        }
    }
}
