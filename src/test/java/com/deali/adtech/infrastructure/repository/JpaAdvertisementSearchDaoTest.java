package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.application.AdvertisementService;
import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.presentation.dto.AdvertisementSearchCondition;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class JpaAdvertisementSearchDaoTest {
    private static final String TEST_PATH = "/Users/admin/temp-image/";

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private JpaAdvertisementSearchDao advertisementSearchDao;

    @BeforeEach
    public void setUp() {
        for(int i = 0 ; i < 30; ++i) {
            Advertisement advertisement = Advertisement.builder()
                    .title(randomString())
                    .expiryDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                    .exposureDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                    .winningBid(randomInteger(1,10))
                    .build();

            advertisementRepository.save(advertisement);
        }
    }

    @Test
    @DisplayName("소재 상세 정보 조회 성공 테스트")
    public void find_advertisement_by_id_success_test() throws Exception {
        Advertisement target = advertisementRepository.findAll().get(0);

        ResponseAdvertisement responseAdvertisement = advertisementSearchDao
                .findAdvertisementById(target.getId());

        assertThat(responseAdvertisement)
            .hasFieldOrPropertyWithValue("id", target.getId())
            .hasFieldOrPropertyWithValue("exposureDate", target.getExposureDate())
            .hasFieldOrPropertyWithValue("expiryDate", target.getExpiryDate())
            .hasFieldOrPropertyWithValue("winningBid", target.getWinningBid())
            .hasFieldOrPropertyWithValue("title", target.getTitle());
    }

    @Test
    @DisplayName("소재 목록 조회 성공 테스트")
    public void search_advertisement_success_test() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ResponseAdvertisement> result =
                advertisementSearchDao.searchAdvertisement(pageable, new AdvertisementSearchCondition());

        assertThat(result.getContent().size())
                .isEqualTo(10);

        assertThat(result.isFirst())
                .isTrue();

        assertThat(result.hasNext())
                .isTrue();
    }


    private String randomString() {
        Random random = new Random();
        return  random.ints(48, 122+1)
                .filter(i -> (i <= 57 || i >= 65) && (i<=90 || i>=97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private int randomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt(max-min+1) + min;
    }
}