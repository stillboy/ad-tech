package com.deali.adtech.infrastructure.util.aspect;

import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Order(2)
@Aspect
public class RedisAspect {
    private final StringRedisTemplate redisTemplate;

    @AfterReturning(pointcut="@annotation(com.deali.adtech.infrastructure.util.annotation.AdvertisementExposed)",
            returning = "exposedList")
    public void increaseRedisExposedCount(List<ResponseCreative> exposedList) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        exposedList.forEach( advertisement -> {
            operations.increment(advertisement.getAdvertisementId().toString());
        });
    }
}
