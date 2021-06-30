package com.deali.adtech.application;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;
import com.deali.adtech.infrastructure.exception.InvalidTitleException;
import com.deali.adtech.infrastructure.exception.InvalidWinningBidException;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import com.deali.adtech.presentation.dto.RequestExtendAdvertisement;
import com.deali.adtech.presentation.dto.RequestPostPoneAdvertisement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

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

    private Long testAdvertisementId;

    @BeforeEach
    public void setUp() {
        LocalDateTime exposureDate = LocalDateTime.now();
        exposureDate = exposureDate.plusDays(30);

        LocalDateTime expiryDate = exposureDate.plusDays(30);

        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle("셋업 데이터");
        request.setWinningBid(5);
        request.setExposureDate(exposureDate);
        request.setExpiryDate(expiryDate);

        String fileName = "temp2.jpg";
        MultipartFile multipartFile = buildMockMultipartFile(fileName);
        request.setImage(multipartFile);

        testAdvertisementId = advertisementServiceImpl.createAdvertisement(request);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("소재 생성 성공 테스트 케이스")
    public void create_advertisement_success_test() {
        /* given */
        RequestCreateAdvertisement request = buildRequestCreatedAdvertisement();
        String fileName = request.getImage().getOriginalFilename();

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

        assertThat(target.getAdvertisementExposeCount())
                .hasFieldOrPropertyWithValue("exposeCount", 0L);

        int lastDot = fileName.lastIndexOf(".");
        String extension = fileName.substring(lastDot+1, fileName.length());

        assertThat(target.getImages().get(0))
                .hasFieldOrPropertyWithValue("extension", extension);
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 케이스 수정할 이미지가 없는 경우")
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
    @DisplayName("소재 수정 실패 테스트 케이스 삭제된 광고일 경우")
    public void edit_advertisement_fail_test_invalid_status() {
        /* given */
        Advertisement advertisement = advertisementRepository.findById(testAdvertisementId)
                .orElseThrow(EntityNotFoundException::new);

        RequestEditAdvertisement request = buildRequestEditAdvertisement();

        /* when */
        advertisement.remove();

        assertThatExceptionOfType(AlreadyRemovedAdvertisementException.class)
                .isThrownBy(()->{
                    advertisementServiceImpl.editAdvertisement(request);
                });

        /* then */
    }

    @Test
    @DisplayName("소재 수정 실패 테스트 케이스 낙찰가가 1~10의 범위가 아닐 경우")
    public void edit_advertisement_fail_test_invalid_winning_bid() {
        /* given */
        RequestEditAdvertisement request = buildRequestEditAdvertisement();
        request.setWinningBid(11);

        /* when */
        assertThatExceptionOfType(InvalidWinningBidException.class)
                .isThrownBy(()->{
                    advertisementServiceImpl.editAdvertisement(request);
                });

        /* then */
    }

    @Test
    @DisplayName("소재 수정 실패 테스트 케이스 수정할 광고 제목이 null 이거나 빈 문자열일 경우 ")
    public void edit_advertisement_final_test_invalid_modified_time() {
        RequestEditAdvertisement request =
                buildRequestEditAdvertisement();

        request.setTitle("     ");

        assertThatExceptionOfType(InvalidTitleException.class)
                .isThrownBy(()->{
                   advertisementServiceImpl.editAdvertisement(request);
                });
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 케이스 이미지가 있는 경우")
    public void edit_advertisement_success_test_with_image()  {
        /* given */
        Advertisement target = advertisementRepository.findById(testAdvertisementId)
                .orElseThrow(EntityNotFoundException::new);

        RequestEditAdvertisement request = buildRequestEditAdvertisement();

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

        assertThat(result.getImages().size())
                .isEqualTo(1);

        assertThat(result.getImages().get(0))
                .hasFieldOrPropertyWithValue("extension", extension)
                .hasFieldOrPropertyWithValue("size", mockMultipartFile.getSize());
    }

    //TODO::수정 필수
    @Disabled
    @Test
    @DisplayName("소재 기간 연기 성공 테스트 케이스")
    public void postpone_advertisement_success_test() {
        /* given */
        Advertisement target = advertisementRepository.findById(testAdvertisementId)
                .orElseThrow(EntityNotFoundException::new);

        RequestPostPoneAdvertisement request = new RequestPostPoneAdvertisement();
        request.setAdvertisementId(target.getId());
        request.setExposureDate(LocalDateTime.from(target.getExposureDate()).plusDays(30));

        Duration originDuration = Duration.between(target.getExposureDate(), target.getExpiryDate());

        /* when */
        //advertisementServiceImpl.postponeAdvertisement(request);

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

    //TODO::수정필수
    @Disabled
    @Test
    @DisplayName("소재 기간 연장 성공 테스트 케이스")
    public void extend_advertisement_success_test()  {
        /* given */
        Advertisement target = advertisementRepository.findById(testAdvertisementId)
                .orElseThrow(EntityNotFoundException::new);

        LocalDateTime newExpiryDate = LocalDateTime.from(target.getExpiryDate()).plusDays(30);

        RequestExtendAdvertisement request = new RequestExtendAdvertisement();
        request.setAdvertisementId(target.getId());
        request.setExpiryDate(newExpiryDate);

        /* when */
        //advertisementServiceImpl.extendAdvertisement(request);

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

    private MultipartFile buildMockMultipartFile(String fileName) {
        MultipartFile file = null;
        File image = new File(TEST_PATH + fileName);

        try(FileInputStream fileInputStream =
                new FileInputStream(image)) {
            file = new MockMultipartFile(fileName, fileName, "jpg", fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return file;
    }

    private RequestCreateAdvertisement buildRequestCreatedAdvertisement() {
        LocalDateTime exposureDate = LocalDateTime.now();
        exposureDate = exposureDate.plusDays(30);

        LocalDateTime expiryDate = exposureDate.plusDays(30);

        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle(randomString());
        request.setWinningBid(1);
        request.setExposureDate(exposureDate);
        request.setExpiryDate(expiryDate);

        String fileName = "temp2.jpg";
        MultipartFile multipartFile = buildMockMultipartFile(fileName);
        request.setImage(multipartFile);

        return request;
    }

    private RequestEditAdvertisement buildRequestEditAdvertisement() {
        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(testAdvertisementId);
        request.setTitle("수정된 제목");
        request.setWinningBid(7);

        return request;
    }

    private String randomString() {
        Random random = new Random();
        return  random.ints(48, 122+1)
                .filter(i -> (i <= 57 || i >= 65) && (i<=90 || i>=97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}