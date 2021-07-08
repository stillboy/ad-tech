package com.deali.adtech.domain;

import java.time.LocalDateTime;

public interface StatusStrategy {
    void changeDuration(Advertisement advertisement, LocalDateTime exposureDate, LocalDateTime expiryDate);
    void validateExposureDate(Advertisement advertisement, LocalDateTime exposureDate);
    void validateExpiryDate(Advertisement advertisement, LocalDateTime expiryDate, LocalDateTime exposureDate);
}
