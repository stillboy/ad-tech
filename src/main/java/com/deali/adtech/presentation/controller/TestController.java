package com.deali.adtech.presentation.controller;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementExposeCount;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.repository.AdvertisementExposeCountRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.infrastructure.util.support.FileUploadSupport;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@RequiredArgsConstructor
@RestController
public class TestController {
    private final AdvertisementRepository repository;
    private final AdvertisementImageRepository imageRepository;
    private final AdvertisementExposeCountRepository exposeCountRepository;
    private final FileUploadSupport fileUploadSupport;

    @Value("${image.advertisement.default-path}")
    private String defaultPath;

    @PostMapping("/test")
    public void setUp() throws IOException {
        LocalDateTime current = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        buildAdvertisement("대기중에서 광고중으로1", 1, current, current.plusDays(30), AdvertisementStatus.WAITING);
        buildAdvertisement("대기중에서 광고중으로2", 2, current, current.plusDays(30), AdvertisementStatus.WAITING);
        buildAdvertisement("대기중에서 광고중으로3", 3, current, current.plusDays(30), AdvertisementStatus.WAITING);

        buildAdvertisement("1분후 광고중으로", 4, current.plusMinutes(1), current.plusDays(30), AdvertisementStatus.WAITING);
        buildAdvertisement("2분후 만료됨으로", 5, current, current.plusMinutes(2), AdvertisementStatus.WAITING);

        buildAdvertisement("만료된 광고", 6, current.minusDays(30), current.minusDays(10), AdvertisementStatus.EXPIRED);
        buildAdvertisement("일시정지 중인 광고",7, current.minusDays(10), current.plusDays(30), AdvertisementStatus.PAUSED);
        buildAdvertisement("일시정지 중에서 1분후 만료됨으로", 7, current.minusDays(30), current.plusMinutes(1), AdvertisementStatus.PAUSED);
        buildAdvertisement("삭제된 광고", 10, current.minusDays(10), current.plusDays(30), AdvertisementStatus.DELETED);
    }

    private void buildAdvertisement(String title, Integer winningBid, LocalDateTime exposureDate,
                                    LocalDateTime expiryDate, AdvertisementStatus status) throws IOException {
        LocalDateTime temp = LocalDateTime.now();

        Advertisement advertisement = Advertisement.builder()
                .title(title)
                .winningBid(winningBid)
                .exposureDate(temp)
                .expiryDate(temp.plusDays(30))
                .build();

        Field statusField = ReflectionUtils.findRequiredField(Advertisement.class, "status");
        Field exposureDateField = ReflectionUtils.findRequiredField(Advertisement.class, "exposureDate");
        Field expiryDateField = ReflectionUtils.findRequiredField(Advertisement.class, "expiryDate");

        ReflectionUtils.setField(exposureDateField, advertisement, exposureDate);
        ReflectionUtils.setField(expiryDateField, advertisement, expiryDate);
        ReflectionUtils.setField(statusField, advertisement, status);

        advertisement = repository.save(advertisement);

        File file = new File(defaultPath+Integer.toString(randInteger())+".jpeg");

        AdvertisementImage image = AdvertisementImage.builder()
                .name(file.getName())
                .path(defaultPath)
                .size(1L)
                .build();

        image.bindAdvertisement(advertisement);

        imageRepository.save(image);

        fileUploadSupport.uploadImage(Files.toByteArray(file), image.getFullPathName());

        AdvertisementExposeCount exposeCount = AdvertisementExposeCount.builder()
                .advertisement(advertisement)
                .build();

        exposeCountRepository.save(exposeCount);
    }

    private int randInteger() {
        return new Random().nextInt(10)+1;
    }
}
