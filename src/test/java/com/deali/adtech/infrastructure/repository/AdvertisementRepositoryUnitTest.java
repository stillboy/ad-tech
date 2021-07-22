package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.Advertisement;
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
public class AdvertisementRepositoryUnitTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Test
    @DisplayName("소재 생성 성공 테스트")
    public void advertisement_save_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement advertisement = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        /* when */
        Advertisement result = advertisementRepository.save(advertisement);

        /* then */
        assertThat(result)
                .hasFieldOrPropertyWithValue("title", advertisement.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", advertisement.getWinningBid())
                .hasFieldOrPropertyWithValue("exposureDate", advertisement.getExposureDate())
                .hasFieldOrPropertyWithValue("expiryDate", advertisement.getExpiryDate())
                .hasFieldOrPropertyWithValue("createdAt", advertisement.getCreatedAt())
                .hasFieldOrPropertyWithValue("modifiedAt", advertisement.getModifiedAt())
                .hasFieldOrPropertyWithValue("status", advertisement.getStatus());
    }

    @Test
    @DisplayName("소재 조회 성공 테스트")
    public void advertisement_find_success_test() throws Exception {
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement advertisement = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        /* when */
        Advertisement result = advertisementRepository.save(advertisement);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Advertisement> target = advertisementRepository.findById(result.getId());

        result = target.orElse(null);

        /* then */
        assertThat(target.isPresent())
                .isTrue();

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", advertisement.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", advertisement.getWinningBid())
                .hasFieldOrPropertyWithValue("exposureDate", advertisement.getExposureDate())
                .hasFieldOrPropertyWithValue("expiryDate", advertisement.getExpiryDate())
                .hasFieldOrPropertyWithValue("createdAt", advertisement.getCreatedAt())
                .hasFieldOrPropertyWithValue("modifiedAt", advertisement.getModifiedAt())
                .hasFieldOrPropertyWithValue("status", advertisement.getStatus());
    }

    @Test
    @DisplayName("소재 수정 성공 테스트")
    public void advertisement_update_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement advertisement = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        String updatedTitle = "신규 광고 수정";
        Integer updatedWinningBid = 9;

        /* when */
        Advertisement result = advertisementRepository.save(advertisement);
        result.editAdvertisement(updatedTitle, updatedWinningBid, exposureDate, expiryDate);

        testEntityManager.flush();
        testEntityManager.clear();

        result = advertisementRepository.findById(result.getId())
                .orElseThrow(EntityNotFoundException::new);
        /* then */

        assertThat(result)
                .hasFieldOrPropertyWithValue("title", updatedTitle)
                .hasFieldOrPropertyWithValue("winningBid", updatedWinningBid)
                .hasFieldOrPropertyWithValue("exposureDate", advertisement.getExposureDate())
                .hasFieldOrPropertyWithValue("expiryDate", advertisement.getExpiryDate())
                .hasFieldOrPropertyWithValue("createdAt", advertisement.getCreatedAt())
                .hasFieldOrPropertyWithValue("modifiedAt", advertisement.getModifiedAt())
                .hasFieldOrPropertyWithValue("status", advertisement.getStatus());
    }

    @Test
    @DisplayName("소재 삭제 성공 테스트")
    public void advertisement_delete_success_test() throws Exception {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement advertisement = Advertisement.builder()
                .title("신규 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        /* when */
        Advertisement result = advertisementRepository.save(advertisement);
        advertisementRepository.deleteById(result.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Advertisement> target = advertisementRepository.findById(result.getId());

        /* then */
        assertThat(target.isPresent())
                .isFalse();
    }
}
