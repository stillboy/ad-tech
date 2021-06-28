package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.presentation.dto.ResponseCreative;

import java.util.HashMap;
import java.util.List;

public interface AdvertisementDocumentRepository {
    List<ResponseCreative> searchTop10Advertisement(double bidRate, double dateRate);
    HashMap<String,Number> searchMinMaxWinningBidAndModifiedAt();
}
