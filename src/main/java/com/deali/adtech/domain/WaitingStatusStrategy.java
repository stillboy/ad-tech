package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.InvalidExpiryDateException;
import com.deali.adtech.infrastructure.exception.InvalidExposureDateException;
import com.deali.adtech.infrastructure.exception.StatusMismatchException;

import java.time.LocalDateTime;

public class WaitingStatusStrategy implements StatusStrategy{
    @Override
    public void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate) {
        //TODO::상태값이 일치하지 않을 경우 익셉션 정의
        if(advertisement.getStatus() != AdvertisementStatus.WAITING) {
            throw new StatusMismatchException();
        }

        validateExposureDate(advertisement, exposureDate);
        validateExpiryDate(advertisement, expiryDate, exposureDate);

        advertisement.changeExposureDate(exposureDate);
        advertisement.changeExpiryDate(expiryDate);
    }

    @Override
    public void validateExposureDate(Advertisement advertisement, LocalDateTime exposureDate) {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime originExposureDate = advertisement.getExposureDate();

        if(exposureDate != null && exposureDate.isEqual(originExposureDate)) return;

        if(exposureDate == null || exposureDate.isBefore(current) || exposureDate.isEqual(current)) {
            throw new InvalidExposureDateException();
        }
    }

    @Override
    public void validateExpiryDate(Advertisement advertisement, LocalDateTime expiryDate, LocalDateTime exposureDate) {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime originExposureDate = advertisement.getExposureDate();
        LocalDateTime originExpiryDate = advertisement.getExpiryDate();

        if(expiryDate != null && expiryDate.isEqual(originExpiryDate)) return;

        if(expiryDate == null || expiryDate.isBefore(current)) {
            throw new InvalidExpiryDateException();
        }

        if(expiryDate.isEqual(exposureDate) || expiryDate.isBefore(exposureDate)) {
            throw new InvalidExpiryDateException();
        }
    }

}
