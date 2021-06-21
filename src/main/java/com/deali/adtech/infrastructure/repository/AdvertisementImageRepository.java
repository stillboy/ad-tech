package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementImageRepository extends JpaRepository<AdvertisementImage, Long> {
    List<AdvertisementImage> findByAdvertisementId(Long advertisementId);
}
