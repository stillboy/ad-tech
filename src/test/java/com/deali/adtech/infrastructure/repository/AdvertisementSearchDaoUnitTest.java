package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.presentation.dto.AdvertisementSearchCondition;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@SpringBootTest
public class AdvertisementSearchDaoUnitTest {
    @Autowired
    private AdvertisementSearchDao advertisementSearchDao;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        for(int i = 0; i < 50; ++i) {
            Advertisement advertisement = Advertisement.builder()
                    .title("testTitle")
                    .winningBid(10)
                    .exposureDate(exposureDate)
                    .expiryDate(expiryDate)
                    .build();

            entityManager.persist(advertisement);

            AdvertisementImage image = AdvertisementImage.builder()
                    .name("temp2.jpg")
                    .path("/User/admin/temp-image")
                    .size(100L)
                    .build();

            image.bindAdvertisement(advertisement);

            entityManager.persist(image);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("소재 목록 조회 성공 테스트")
    public void search_advertisement_success_test() throws Exception {
        /* given */
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        AdvertisementSearchCondition condition = new AdvertisementSearchCondition();

        /* when */
        Page<ResponseAdvertisement> results =
                advertisementSearchDao.searchAdvertisement(pageable, condition);

        /* then */
        assertThat(results.getContent())
                .hasSize(size);
    }

    @Test
    @DisplayName("소재 상세 정보 조회 성공 테스트")
    public void find_advertisement_by_id() throws Exception {
        /* given */

        /* when */

        /* then */
    }
}
