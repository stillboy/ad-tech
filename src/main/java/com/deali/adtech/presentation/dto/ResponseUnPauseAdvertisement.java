package com.deali.adtech.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseUnPauseAdvertisement {
    private String message;
    private Long advertisementId;
}
