package com.deali.adtech.application;

import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import org.springframework.web.multipart.MultipartFile;

public interface AdvertisementService {
    Long createAdvertisement(RequestCreateAdvertisement requestCreateAdvertisement);
}
