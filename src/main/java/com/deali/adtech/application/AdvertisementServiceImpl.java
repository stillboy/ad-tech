package com.deali.adtech.application;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.domain.ExposeCount;
import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;
import com.deali.adtech.infrastructure.exception.InvalidPostponeRequestException;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.infrastructure.repository.ExposeCountRepository;
import com.deali.adtech.infrastructure.util.AdvertisementMapper;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdvertisementServiceImpl implements AdvertisementService{
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementImageRepository imageRepository;
    private final ExposeCountRepository exposeCountRepository;

    @Value("${image.advertisement.default-path}")
    private String defaultPath;

    @Override
    public Long createAdvertisement(@NonNull RequestCreateAdvertisement requestCreateAdvertisement) {

        Advertisement advertisement = AdvertisementMapper.INSTANCE
                                        .dtoToEntity(requestCreateAdvertisement);

        advertisement = advertisementRepository.save(advertisement);

        AdvertisementImage advertisementImage = AdvertisementMapper.INSTANCE
                .fileToEntity(requestCreateAdvertisement.getImage(), defaultPath);

        advertisementImage.bindAdvertisement(advertisement);

        advertisementImage = imageRepository.save(advertisementImage);

        try {

            advertisementImage.uploadImageFile(defaultPath,
                    requestCreateAdvertisement.getImage().getBytes());

        } catch (IOException exception) {
            throw new RuntimeException();
        }

        ExposeCount exposeCount = ExposeCount.builder()
                .advertisement(advertisement)
                .build();

        exposeCount = exposeCountRepository.save(exposeCount);

        return advertisement.getId();
    }

    @Override
    public void editAdvertisement(RequestEditAdvertisement requestEditAdvertisement) {
        Advertisement advertisement = getAdvertisementEntity(requestEditAdvertisement.getId());

        advertisement.editAdvertisement(requestEditAdvertisement.getTitle(),
                requestEditAdvertisement.getWinningBid());

        MultipartFile newImage = requestEditAdvertisement.getNewImage();

        if(newImage != null && !newImage.isEmpty()) {
            exchangeOneImage(advertisement.getImages().get(0), newImage);
        }
    }

    @Override
    public void postponeAdvertisement(Long advertisementId) {
        Advertisement advertisement = getAdvertisementEntity(advertisementId);

        advertisement.postpone();
    }

    //TODO::이미지 처리 관련 로직 분리시키기
    protected void uploadImage(MultipartFile image) {
        try(FileOutputStream outputStream =
                    new FileOutputStream(defaultPath + image.getName())) {

            byte[] dataStream = image.getBytes();
            outputStream.write(dataStream);

        } catch(IOException exception) {
            throw new RuntimeException();
        }
    }

    protected void exchangeOneImage(AdvertisementImage advertisementImage,
                                    MultipartFile newImage) {
        uploadImage(newImage);

        //TODO::익셉션 정의 필요
        if(!deleteImage(advertisementImage)) {
            throw new RuntimeException();
        }

        advertisementImage.editNameAndExtension(newImage.getName());
        advertisementImage.changeSize(newImage.getSize());
    }

    protected boolean deleteImage(AdvertisementImage advertisementImage) {
        File file = new File(advertisementImage.getFullPathName());

        //TODO::익셉션 정의 필요
        if(!file.exists()) {
            throw new RuntimeException();
        }

        return file.delete();
    }

    private Advertisement getAdvertisementEntity(Long key) {
        return advertisementRepository.findById(key).orElseThrow(EntityNotFoundException::new);
    }
}
