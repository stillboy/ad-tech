package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class AdvertisementImageRepositoryUnitTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private AdvertisementImageRepository imageRepository;

    @Test
    @DisplayName("소재 이미지 생성 성공 테스트")
    public void advertisement_image_create_success_test() throws Exception {
        /* given */
        Advertisement advertisement = mockAdvertisement();
        AdvertisementImage advertisementImage = AdvertisementImage.builder()
                .name("test.jpg")
                .path("/User/temp-image/")
                .size(1000L)
                .build();

        /* when */
        advertisement = advertisementRepository.save(advertisement);
        advertisementImage.bindAdvertisement(advertisement);
        AdvertisementImage result = imageRepository.save(advertisementImage);

        /* then */
        assertThat(result)
                .hasFieldOrPropertyWithValue("name", advertisementImage.getName())
                .hasFieldOrPropertyWithValue("extension", advertisementImage.getExtension())
                .hasFieldOrPropertyWithValue("path", advertisementImage.getPath())
                .hasFieldOrPropertyWithValue("size", advertisementImage.getSize());
    }

   @Test
   @DisplayName("소재 이미지 조회 성공 테스트")
   public void advertisement_image_find_success_test() throws Exception {
       Advertisement advertisement = mockAdvertisement();
       AdvertisementImage advertisementImage = AdvertisementImage.builder()
               .name("test.jpg")
               .path("/User/temp-image/")
               .size(1000L)
               .build();

       /* when */
       advertisement = advertisementRepository.save(advertisement);
       advertisementImage.bindAdvertisement(advertisement);
       AdvertisementImage result = imageRepository.save(advertisementImage);

       entityManager.flush();
       entityManager.clear();

       Optional<AdvertisementImage> target = imageRepository.findById(result.getId());

       /* then */
       assertThat(target.isPresent())
               .isTrue();

       assertThat(target.get())
               .isNotNull()
               .hasFieldOrPropertyWithValue("name", advertisementImage.getName())
               .hasFieldOrPropertyWithValue("extension", advertisementImage.getExtension())
               .hasFieldOrPropertyWithValue("path", advertisementImage.getPath())
               .hasFieldOrPropertyWithValue("size", advertisementImage.getSize());
   }

    @Test
    @DisplayName("소재 이미지 수정 성공 테스트")
    public void advertisement_image_update_success_test() throws Exception {
        /* given */
        Advertisement advertisement = mockAdvertisement();
        AdvertisementImage advertisementImage = AdvertisementImage.builder()
                .name("test.jpg")
                .path("/User/temp-image/")
                .size(1000L)
                .build();

        String updatedName = "updatedTest.jpg";

        advertisement = advertisementRepository.save(advertisement);
        advertisementImage.bindAdvertisement(advertisement);
        AdvertisementImage result = imageRepository.save(advertisementImage);

        /* when */
        advertisementImage.editNameAndExtension(updatedName);

        entityManager.flush();
        entityManager.clear();

        AdvertisementImage target = imageRepository.findById(advertisementImage.getId())
                .orElseThrow(EntityNotFoundException::new);

        /* then */
        assertThat(target)
                .hasFieldOrPropertyWithValue("name", advertisementImage.getName())
                .hasFieldOrPropertyWithValue("extension", advertisementImage.getExtension());
    }

    @Test
    @DisplayName("소재 이미지 삭제 성공 테스트")
    public void advertisement_image_delete_success_test() throws Exception {
        /* given */
        Advertisement advertisement = mockAdvertisement();
        AdvertisementImage advertisementImage = AdvertisementImage.builder()
                .name("test.jpg")
                .path("/User/temp-image/")
                .size(1000L)
                .build();

        advertisement = advertisementRepository.save(advertisement);
        advertisementImage.bindAdvertisement(advertisement);
        AdvertisementImage result = imageRepository.save(advertisementImage);

        /* when */
        imageRepository.delete(result);

        entityManager.flush();
        entityManager.clear();

        Optional<AdvertisementImage> target = imageRepository.findById(result.getId());

        /* then */
        assertThat(target.isPresent())
                .isFalse();
    }

    private Advertisement mockAdvertisement() {
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        return Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();
    }
}
