package com.deali.adtech.application;

import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;

public interface AdvertisementService {
    Long createAdvertisement(RequestCreateAdvertisement requestCreateAdvertisement);
    void editAdvertisement(RequestEditAdvertisement requestEditAdvertisement);
    void postponeAdvertisement(Long advertisementId);
}
