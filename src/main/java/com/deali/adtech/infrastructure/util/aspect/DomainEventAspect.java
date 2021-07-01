package com.deali.adtech.infrastructure.util.aspect;

import com.deali.adtech.infrastructure.util.event.Events;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(0)
@Component
public class DomainEventAspect implements ApplicationEventPublisherAware{
    private ApplicationEventPublisher applicationEventPublisher;
    private ThreadLocal<Boolean> appliedThreadLocal = new ThreadLocal<>();

    @Around("execution(* com.deali.adtech.application.AdvertisementService.*(..))")
    public Object setUpEventPublisher(ProceedingJoinPoint joinPoint) throws Throwable {
        Boolean appliedValue = appliedThreadLocal.get();
        boolean hasEventPublisher = false;

        if(appliedValue != null && appliedValue) {
            hasEventPublisher = true;
        } else {
            hasEventPublisher = false;
            appliedThreadLocal.set(true);
        }

        if(!hasEventPublisher) {

            Events.setPublisher(applicationEventPublisher);
        }

        try {
            return joinPoint.proceed();
        } finally {
            if(!hasEventPublisher) {
                Events.reset();
                appliedThreadLocal.remove();
            }
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
