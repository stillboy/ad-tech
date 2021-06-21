package com.deali.adtech.application;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import com.deali.adtech.presentation.dto.RequestExtendAdvertisement;
import com.deali.adtech.presentation.dto.RequestPostPoneAdvertisement;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class AdvertisementServiceTest {
    private static final String TEST_PATH = "/Users/admin/temp-image/";
    @Autowired
    private AdvertisementService advertisementServiceImpl;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception{
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle("셋업 데이터");
        request.setWinningBid(5);
        request.setExposureDate(LocalDateTime.of(2021,6,18,12,00));
        request.setExpiryDate(LocalDateTime.of(2021,6,30,12,00));

        String fileName = "temp2.jpg";
        MultipartFile multipartFile = buildMockMultipartFile(fileName);
        request.setImage(multipartFile);

        advertisementServiceImpl.createAdvertisement(request);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("소재 생성 성공 테스트 케이스")
    public void create_advertisement_success_test() throws Exception{
        /* given */
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle("테스트1");
        request.setWinningBid(1);
        request.setExposureDate(LocalDateTime.of(2021,6,18,12,00));
        request.setExpiryDate(LocalDateTime.of(2021,6,30,12,00));

        String fileName = "temp2.jpg";
        MultipartFile multipartFile = buildMockMultipartFile(fileName);
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
                .hasFieldOrPropertyWithValue("status", AdvertisementStatus.WAITING);

        assertThat(target.getExposeCount())
                .hasFieldOrPropertyWithValue("exposeCount", 0L);

        int lastDot = fileName.lastIndexOf(".");
        String extension = fileName.substring(lastDot+1, fileName.length());

        assertThat(target.getImages().get(0))
                .hasFieldOrPropertyWithValue("extension", extension);
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 케이스 이미지가 없는 경우")
    public void edit_advertisement_success_test_no_images() {
        /* given */
        Advertisement target = advertisementRepository.findAll().get(0);

        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(target.getId());
        request.setTitle("수정된 제목");
        request.setWinningBid(7);

        /* when */
        advertisementServiceImpl.editAdvertisement(request);
        entityManager.flush();
        entityManager.clear();

        Advertisement result = advertisementRepository.getById(target.getId());

        /* then */

        assertThat(result)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", request.getWinningBid());

        assertThat(result.getCreatedAt())
                .isNotEqualTo(result.getModifiedAt());
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 케이스 이미지가 있는 경우")
    public void edit_advertisement_success_test_with_image() throws Exception {
        /* given */
        Advertisement target = advertisementRepository.findAll().get(0);

        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(target.getId());
        request.setTitle("수정된 제목");
        request.setWinningBid(7);

        String fileName = "editedImage.jpg";
        int lastDot = fileName.lastIndexOf(".");
        String extension = fileName.substring(lastDot+1, fileName.length());

        MultipartFile mockMultipartFile = buildMockMultipartFile(fileName);
        request.setNewImage(mockMultipartFile);
        /* when */
        advertisementServiceImpl.editAdvertisement(request);
        entityManager.flush();
        entityManager.clear();

        Advertisement result = advertisementRepository.getById(target.getId());

        /* then */

        assertThat(result)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", request.getWinningBid());

        assertThat(result.getCreatedAt())
                .isNotEqualTo(result.getModifiedAt());

        assertThat(result.getImages().size())
                .isEqualTo(1);

        assertThat(result.getImages().get(0))
                .hasFieldOrPropertyWithValue("extension", extension)
                .hasFieldOrPropertyWithValue("size", mockMultipartFile.getSize());
    }

    @Test
    @DisplayName("소재 기간 연기 성공 테스트 케이스")
    public void postpone_advertisement_success_test() {
        /* given */
        Advertisement target = advertisementRepository.findAll().get(0);

        RequestPostPoneAdvertisement request = new RequestPostPoneAdvertisement();
        request.setAdvertisementId(target.getId());
        request.setExposureDate(LocalDateTime.of(2021,6,20,12,00));

        Duration originDuration = Duration.between(target.getExposureDate(), target.getExpiryDate());

        /* when */
        advertisementServiceImpl.postponeAdvertisement(request);

        entityManager.flush();
        entityManager.clear();

        target = advertisementRepository.findById(request.getAdvertisementId())
                .orElseThrow(EntityNotFoundException::new);

        /* then */
        assertThat(target)
                .hasFieldOrPropertyWithValue("exposureDate", request.getExposureDate());

        assertThat(Duration.between(target.getExposureDate(), target.getExpiryDate()))
                .isEqualTo(originDuration);
    }

    @Test
    @DisplayName("소재 기간 연장 선공 테스트 케이스")
    public void extend_advertisement_success_test() {
        /* given */
        Advertisement target = advertisementRepository.findAll().get(0);

        LocalDateTime newExpiryDate = LocalDateTime.of(2021,7,25,12,00);

        RequestExtendAdvertisement request = new RequestExtendAdvertisement();
        request.setAdvertisementId(target.getId());
        request.setExpiryDate(newExpiryDate);

        /* when */
        advertisementServiceImpl.extendAdvertisement(request);

        entityManager.flush();
        entityManager.clear();

        target = advertisementRepository.findById(target.getId())
                .orElseThrow(EntityNotFoundException::new);

        /* then */
        assertThat(target)
                .hasFieldOrPropertyWithValue("expiryDate", newExpiryDate);
    }

    @Test
    @DisplayName("소재 삭제 성공 테스트 케이스")
    public void remove_advertisement_success_test() {
        Advertisement advertisement = advertisementRepository.findAll().get(0);
        Long targetId = advertisement.getId();

        advertisementServiceImpl.removeAdvertisement(advertisement.getId());
        entityManager.flush();
        entityManager.clear();

        Advertisement target = advertisementRepository.findById(targetId)
                .orElseThrow(EntityNotFoundException::new);

        assertThat(target)
                .hasFieldOrPropertyWithValue("status", AdvertisementStatus.DELETED);
    }

    private MultipartFile buildMockMultipartFile(String fileName) throws Exception{
        return new MockMultipartFile(fileName, new FileInputStream(new File(TEST_PATH+fileName)));
    }
}