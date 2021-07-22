package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
}
