package com.deali.adtech.infrastructure.util;

import com.deali.adtech.domain.AdvertisementRemovedEvent;

public interface AdvertisementNotifier {
    void removeAdvertisementDocument(final AdvertisementRemovedEvent event);
}
