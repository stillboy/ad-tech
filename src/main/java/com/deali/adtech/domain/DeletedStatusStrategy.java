package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;

import java.time.LocalDateTime;

public class DeletedStatusStrategy implements StatusStrategy{
    @Override
    public void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate) {
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