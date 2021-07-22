package com.deali.adtech.infrastructure.util.aspect;

import com.deali.adtech.domain.ExposedLog;
import com.deali.adtech.infrastructure.repository.ExposedLogRepository;
import com.deali.adtech.infrastructure.util.mapper.ExposedLogMapper;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Order(1)
@Aspect
public class LoggingAspect {
    private final ExposedLogRepository exposedLogRepository;
    private final ExposedLogMapper logMapper;

    @Transactional
    @AfterReturning(
            pointcut = "@annotation(com.deali.adtech.infrastructure.util.annotation.AdvertisementExposed)",
            returning = "exposedList")
    public void writeAdvertisementExposedLog(List<ResponseCreative> exposedList) {
        exposedList.forEach(advertisement -> {
            ExposedLog log = logMapper.toLog(advertisement);
            exposedLogRepository.save(log);
        });
    }
}
