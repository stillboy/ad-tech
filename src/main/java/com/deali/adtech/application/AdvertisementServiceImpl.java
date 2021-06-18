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

        uploadImage(requestCreateAdvertisement.getImage());

        AdvertisementImage advertisementImage = AdvertisementMapper.INSTANCE
                .fileToEntity(requestCreateAdvertisement.getImage(), defaultPath);

        advertisementImage.bindAdvertisement(advertisement);

        advertisementImage = imageRepository.save(advertisementImage);

        ExposeCount exposeCount = ExposeCount.builder()
                .advertisement(advertisement)
                .build();

        exposeCount = exposeCountRepository.save(exposeCount);

        return advertisement.getId();
    }

    @Override
    public void editAdvertisement(RequestEditAdvertisement requestEditAdvertisement) {
        //TODO:: 삭제된 광고는 수정할 수 없음, 수정일이 광고를 수정한 날짜? 혹은 내가 노출기간을 변경한 날짜?
        Advertisement advertisement = advertisementRepository
                .findById(requestEditAdvertisement.getId())
                .orElseThrow(EntityNotFoundException::new);

        if(advertisement.getStatus() == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        advertisement.editTitle(requestEditAdvertisement.getTitle());
        advertisement.changeWinningBid(requestEditAdvertisement.getWinningBid());
        advertisement.updateModifiedAt(LocalDateTime.now());

        //TODO::수정된 애들만 따로 조회할 수 있게 할까 아니면 그냥 한꺼번에 다 반영시켜버릴까? => 일단 한꺼번에 다 반영

        //TODO:: 수정할 이미지가 존재하면 분기 처리
        MultipartFile newImage = requestEditAdvertisement.getNewImage();

        if(newImage != null && !newImage.isEmpty()) {
            exchangeOneImage(advertisement.getImages().get(0), newImage);
        }
    }

    @Override
    public void postponeAdvertisement(Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(EntityNotFoundException::new);

        //TODO::고려사항
        /**
         * 1. 삭제된 광고는 연기시킬 수 없음
         * 2. 광고중인 광고를 연기시킬 경우 현재 요청한 시간 부터 광고 기간까지의 차이만큼 다시 더해줘야한다.
         * 3. 광고 기간이 만료된 광고도 연기시킬 수 없음
         * */
        if(advertisement.getStatus() == AdvertisementStatus.DELETED ||
                advertisement.getStatus() == AdvertisementStatus.EXPIRATION) {
            throw new InvalidPostponeRequestException();
        }


    }

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
}
