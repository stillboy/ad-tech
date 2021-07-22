package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.presentation.dto.ResponseCreative;

import java.util.List;

public interface AdvertisementDocumentRepository {
    List<ResponseCreative> searchTop10Advertisement();
    void remove(AdvertisementDocument document);
    void update(AdvertisementDocument document);
    void insert(AdvertisementDocument document);
}
