package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.presentation.dto.AdvertisementSearchCondition;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdvertisementSearchDao {
    Page<ResponseAdvertisement> searchAdvertisement(Pageable pageable, AdvertisementSearchCondition searchCondition);
    ResponseAdvertisement findAdvertisementById(Long advertisementId);
}
