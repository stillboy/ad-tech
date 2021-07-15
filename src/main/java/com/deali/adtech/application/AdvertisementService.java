package com.deali.adtech.application;

import com.deali.adtech.domain.*;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementExposeCountRepository;
import com.deali.adtech.infrastructure.util.event.AdvertisementChangedEvent;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementMapper;
import com.deali.adtech.infrastructure.util.support.FileUploadSupport;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementImageRepository imageRepository;
    private final AdvertisementExposeCountRepository advertisementExposeCountRepository;
    private final AdvertisementMapper advertisementMapper;
    private final FileUploadSupport fileUploadSupport;

    @Value("${image.advertisement.default-path}")
    private String defaultPath;

    public Long createAdvertisement(@NonNull RequestCreateAdvertisement requestCreateAdvertisement) {
        Advertisement advertisement = advertisementMapper.dtoToEntity(requestCreateAdvertisement);

        advertisement = advertisementRepository.save(advertisement);

        AdvertisementImage advertisementImage = advertisementMapper
                .fileToEntity(requestCreateAdvertisement.getImage(), defaultPath);

        advertisementImage.bindAdvertisement(advertisement);

        advertisementImage = imageRepository.save(advertisementImage);

       fileUploadSupport.uploadMultipartFileImage(requestCreateAdvertisement.getImage(),
               advertisementImage.getFullPathName());

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

        if(newImage != null && !newImage.isEmpty()) {
            AdvertisementImage advertisementImage = getAdvertisementImageEntity(advertisement);

            String oldFilePath = advertisementImage.getFullPathName();
            advertisementImage.changeLocation(newImage.getOriginalFilename(), defaultPath);

            String newFilePath = advertisementImage.getFullPathName();
            advertisementImage.changeSize(newImage.getSize());

            fileUploadSupport.exchangeMultipartFileImage(newImage, oldFilePath, newFilePath);
        }
    }

    public void pauseAdvertisement(@NonNull Long advertisementId) {
        Advertisement advertisement = getAdvertisementEntity(advertisementId);
        advertisement.pause();
    }

    public void removeAdvertisement(@NonNull Long advertisementId) {
        Advertisement advertisement = getAdvertisementEntity(advertisementId);
        advertisement.remove();
    }

    private Advertisement getAdvertisementEntity(Long key) {
        return advertisementRepository.findById(key).orElseThrow(EntityNotFoundException::new);
    }

    private AdvertisementImage getAdvertisementImageEntity(Advertisement advertisement) {
        List<AdvertisementImage> images =
                imageRepository.findByAdvertisementId(advertisement.getId());

        return images.get(0);
    }
}
