package com.deali.adtech.application;

import com.deali.adtech.domain.ExposedLog;
import com.deali.adtech.infrastructure.repository.ExposedLogRepository;
import com.deali.adtech.infrastructure.repository.MongoAdvertisementDocumentRepository;
import com.deali.adtech.infrastructure.util.annotation.AdvertisementExposed;
import com.deali.adtech.infrastructure.util.mapper.ExposedLogMapper;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdvertisementPoolService {
    private final MongoAdvertisementDocumentRepository mongoRepository;
    private final StringRedisTemplate redisTemplate;
    private final ExposedLogRepository logRepository;
    private final ExposedLogMapper logMapper;

    @AdvertisementExposed
    public List<ResponseCreative> getTop10Advertisement() {
        List<ResponseCreative> results = mongoRepository.searchTop10Advertisement();

        return results;
    }
}
