package com.deali.adtech.domain.service;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;
import com.deali.adtech.infrastructure.exception.StatusMismatchException;

import java.time.LocalDateTime;

public class DeletedStatusStrategy implements StatusStrategy {
    @Override
    public void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate) {
        if(advertisement.getStatus() != AdvertisementStatus.DELETED) {
            throw new StatusMismatchException();
        }

        throw new AlreadyRemovedAdvertisementException();
    }

    @Override
    public void validateExposureDate(Advertisement advertisement, LocalDateTime exposureDate) {
        throw new AlreadyRemovedAdvertisementException();
    }

    @Override
    public void validateExpiryDate(Advertisement advertisement, LocalDateTime expiryDate, LocalDateTime exposureDate) {
        throw new AlreadyRemovedAdvertisementException();
    }
}
