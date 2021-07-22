package com.deali.adtech.infrastructure.util.event;

import org.springframework.context.ApplicationEventPublisher;

public class Events {
    private static ThreadLocal<ApplicationEventPublisher> publisherThreadLocal
            = new ThreadLocal<>();

    public static void raise(DomainEvent event) {
        if(event == null) return;

        if(publisherThreadLocal.get() != null) {
            publisherThreadLocal.get().publishEvent(event);
        }
    }

    public static void setPublisher(ApplicationEventPublisher publisher) {
        publisherThreadLocal.set(publisher);
    }

    public static void reset() {
        publisherThreadLocal.remove();
    }
}
