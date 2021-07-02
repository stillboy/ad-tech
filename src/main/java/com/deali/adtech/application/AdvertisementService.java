package com.deali.adtech.application;

import com.deali.adtech.domain.*;
import com.deali.adtech.infrastructure.exception.ImageUploadFailureException;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementExposeCountRepository;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementMapper;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final AdvertisementMapper advertisementMapper;

    @Value("${image.advertisement.default-path}")
    private String defaultPath;

    public Long createAdvertisement(@NonNull RequestCreateAdvertisement requestCreateAdvertisement) {
        Advertisement advertisement = advertisementMapper.dtoToEntity(requestCreateAdvertisement);

        advertisement = advertisementRepository.save(advertisement);

        AdvertisementImage advertisementImage = advertisementMapper
                .fileToEntity(requestCreateAdvertisement.getImage(), defaultPath);

        advertisementImage.bindAdvertisement(advertisement);

        advertisementImage = imageRepository.save(advertisementImage);

        try {
            advertisementImage.uploadImageFile(requestCreateAdvertisement.getImage().getBytes());
        } catch (Exception exception) {
            throw new ImageUploadFailureException();
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
                requestEditAdvertisement.getWinningBid(),
                requestEditAdvertisement.getExposureDate(),
                requestEditAdvertisement.getExpiryDate());

        MultipartFile newImage = requestEditAdvertisement.getNewImage();

        //TODO::로직 변경 필요, 도메인 서비스로 따로 로직을 분리하면 좋을 것으로 생각
        if(newImage != null && !newImage.isEmpty()) {
            AdvertisementImage advertisementImage = getAdvertisementImageEntity(advertisement);
            try {
                advertisementImage.exchangeImage(newImage.getOriginalFilename(), newImage.getSize(), newImage.getBytes());
            } catch (IOException exception) {
                throw new RuntimeException();
            }
        }
    }

    public void removeAdvertisement(@NonNull Long advertisementId) {
        Advertisement advertisement = getAdvertisementEntity(advertisementId);
        advertisement.remove();
    }

    private Advertisement getAdvertisementEntity(Long key) {
        return advertisementRepository.findById(key).orElseThrow(EntityNotFoundException::new);
    }

    private AdvertisementImage getAdvertisementImageEntity(Advertisement advertisement) {
        return advertisement.getImages().get(0);
    }
}
