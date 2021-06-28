package com.deali.adtech.application;

import com.deali.adtech.infrastructure.repository.MongoAdvertisementDocumentRepository;
import com.deali.adtech.infrastructure.util.annotation.AdvertisementExposed;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdvertisementPoolService {
    private final MongoAdvertisementDocumentRepository mongoRepository;

    @Value("${pool.bid-rate}")
    private double bidRate;
    @Value("${pool.date-rate}")
    private double dateRate;

    @AdvertisementExposed
    public List<ResponseCreative> getTop10Advertisement() {
        List<ResponseCreative> results =
                mongoRepository.searchTop10Advertisement(bidRate, dateRate);

        return results;
    }
}
