package com.deali.adtech.presentation.dto;

import lombok.Data;

@Data
public class ResponseEditAdvertisement {
    private String message;
    private Long advertisementId;

    public ResponseEditAdvertisement(String message, Long advertisementId) {
        this.message = message;
        this.advertisementId = advertisementId;
    }
}
