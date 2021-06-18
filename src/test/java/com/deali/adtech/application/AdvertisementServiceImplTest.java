package com.deali.adtech.application;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class AdvertisementServiceImplTest {
    private static final String TEST_PATH = "/Users/admin/temp-image/";
    @Autowired
    private AdvertisementService advertisementServiceImpl;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void create_advertisement_test() throws Exception{
        /* given */
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle("테스트1");
        request.setWinningBid(1);
        request.setCreatedAt(LocalDateTime.of(2021,6,18,12,00));
        request.setExpiryDate(LocalDateTime.of(2021,6,30,12,00));
        MultipartFile multipartFile =
                new MockMultipartFile("temp.jpg", "originFileName", "jpg",
                        new FileInputStream(new File(TEST_PATH+"temp.jpg")));
        request.setImage(multipartFile);

        /* when */
        Long advertisementId = advertisementServiceImpl.createAdvertisement(request);

        entityManager.flush();
        entityManager.clear();

        Advertisement target = advertisementRepository.findById(advertisementId)
                .orElseThrow(EntityNotFoundException::new);

        /* then */
        assertThat(target)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", request.getWinningBid())
                .hasFieldOrPropertyWithValue("createdAt", request.getCreatedAt())
                .hasFieldOrPropertyWithValue("modifiedAt", request.getCreatedAt())
                .hasFieldOrPropertyWithValue("status", AdvertisementStatus.WAITING);

        assertThat(target.getExposeCount())
                .hasFieldOrPropertyWithValue("exposeCount", 0L);
    }
}