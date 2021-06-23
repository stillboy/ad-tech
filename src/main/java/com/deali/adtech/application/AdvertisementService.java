package com.deali.adtech.application;

import com.deali.adtech.domain.*;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementExposeCountRepository;
import com.deali.adtech.infrastructure.util.AdvertisementMapper;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import com.deali.adtech.presentation.dto.RequestExtendAdvertisement;
import com.deali.adtech.presentation.dto.RequestPostPoneAdvertisement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Transactional
@RequiredArgsConstructor
@Service
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementImageRepository imageRepository;
    private final AdvertisementExposeCountRepository advertisementExposeCountRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${image.advertisement.default-path}")
    private String defaultPath;

    public Long createAdvertisement(@NonNull RequestCreateAdvertisement requestCreateAdvertisement) {

        Advertisement advertisement = AdvertisementMapper.INSTANCE
                                        .dtoToEntity(requestCreateAdvertisement);

        advertisement = advertisementRepository.save(advertisement);

        AdvertisementImage advertisementImage = AdvertisementMapper.INSTANCE
                .fileToEntity(requestCreateAdvertisement.getImage(), defaultPath);

        advertisementImage.bindAdvertisement(advertisement);

        advertisementImage = imageRepository.save(advertisementImage);

        try {
            advertisementImage.uploadImageFile(requestCreateAdvertisement.getImage().getBytes());
        } catch (IOException exception) {
            //TODO::이미지 업로드 익셉션 정의
            throw new RuntimeException();
        }

        AdvertisementExposeCount advertisementExposeCount = AdvertisementExposeCount.builder()
                .advertisement(advertisement)
                .build();

        advertisementExposeCount = advertisementExposeCountRepository.save(advertisementExposeCount);

        return advertisement.getId();
    }

    public void editAdvertisement(@NonNull RequestEditAdvertisement requestEditAdvertisement) {
        Advertisement advertisement = getAdvertisementEntity(requestEditAdvertisement.getId());

        advertisement.editAdvertisement(requestEditAdvertisement.getTitle(),
                requestEditAdvertisement.getWinningBid());

        MultipartFile newImage = requestEditAdvertisement.getNewImage();

        if(newImage != null && !newImage.isEmpty()) {
            AdvertisementImage advertisementImage = getAdvertisementImageEntity(advertisement);
            try {
                advertisementImage.exchangeImage(newImage.getOriginalFilename(), newImage.getSize(), newImage.getBytes());
            } catch (IOException exception) {
                throw new RuntimeException();
            }
        }
    }

    public void postponeAdvertisement(@NonNull RequestPostPoneAdvertisement requestPostPoneAdvertisement) {
        Advertisement advertisement = getAdvertisementEntity(requestPostPoneAdvertisement.getAdvertisementId());
        advertisement.postpone(requestPostPoneAdvertisement.getExposureDate());
    }

    public void extendAdvertisement(@NonNull RequestExtendAdvertisement requestExtendAdvertisement) {
        Advertisement advertisement = getAdvertisementEntity(requestExtendAdvertisement.getAdvertisementId());
        advertisement.extend(requestExtendAdvertisement.getExpiryDate());
    }

    public void removeAdvertisement(@NonNull Long advertisementId) {
        Advertisement advertisement = getAdvertisementEntity(advertisementId);
        advertisement.remove();
        //TODO::이것도 나중에는 분리하는게 맞을거 같은데?
        eventPublisher.publishEvent(new AdvertisementRemovedEvent(advertisement));
    }

    private Advertisement getAdvertisementEntity(Long key) {
        return advertisementRepository.findById(key).orElseThrow(EntityNotFoundException::new);
    }

    private AdvertisementImage getAdvertisementImageEntity(Advertisement advertisement) {
        return advertisement.getImages().get(0);
    }
}
