package com.deali.adtech.presentation.dto;

import com.deali.adtech.domain.AdvertisementStatus;
import lombok.Data;

@Data
public class AdvertisementSearchCondition {
    private String title;
    private AdvertisementStatus status;
}
