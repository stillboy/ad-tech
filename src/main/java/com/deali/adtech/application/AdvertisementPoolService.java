package com.deali.adtech.application;

import com.deali.adtech.infrastructure.repository.AdvertisementDocumentMongoRepository;
import com.deali.adtech.infrastructure.util.annotation.AdvertisementExposed;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdvertisementPoolService {
    private final AdvertisementDocumentMongoRepository advertisementDocumentMongoRepository;
    private final StringRedisTemplate redisTemplate;

    @AdvertisementExposed
    public List<ResponseCreative> getTop10Advertisement() {
        List<ResponseCreative> results =
                advertisementDocumentMongoRepository.searchTop10Advertisement();

        return results;
    }
}
