package com.deali.adtech.domain.service;

import com.deali.adtech.domain.Advertisement;

import java.time.LocalDateTime;

public interface StatusStrategy {
    void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate);
    void validateExposureDate(Advertisement advertisement, LocalDateTime exposureDate);
    void validateExpiryDate(Advertisement advertisement, LocalDateTime expiryDate, LocalDateTime exposureDate);
}
