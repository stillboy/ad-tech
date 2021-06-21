package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.application.AdvertisementService;
import com.deali.adtech.presentation.dto.AdvertisementSearchCondition;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import javax.xml.ws.Response;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("소재 상세 정보 조회 성공 테스트")
    public void find_advertisement_by_id_success_test() throws Exception {
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle("셋업 데이터");
        request.setWinningBid(5);
        request.setExposureDate(LocalDateTime.of(2021,6,18,12,00));
        request.setExpiryDate(LocalDateTime.of(2021,6,30,12,00));

        String fileName = "temp2.jpg";
        MultipartFile multipartFile = buildMockMultipartFile(fileName);
        request.setImage(multipartFile);

        Long targetId = advertisementService.createAdvertisement(request);

        entityManager.flush();
        entityManager.clear();

        ResponseAdvertisement responseAdvertisement = advertisementSearchDao
                .findAdvertisementById(targetId);

        assertThat(responseAdvertisement)
            .hasFieldOrPropertyWithValue("id", targetId)
            .hasFieldOrPropertyWithValue("exposureDate", request.getExposureDate())
            .hasFieldOrPropertyWithValue("expiryDate", request.getExpiryDate())
            .hasFieldOrPropertyWithValue("winningBid", request.getWinningBid())
            .hasFieldOrPropertyWithValue("title", request.getTitle());

        assertThat(responseAdvertisement.getImages().size())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("소재 목록 조회 성공 테스트")
    public void search_advertisement_success_test() throws Exception {
        buildRandomAdvertisementList();

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

    private MultipartFile buildMockMultipartFile(String fileName) throws Exception{
        return new MockMultipartFile(fileName, new FileInputStream(new File(TEST_PATH+fileName)));
    }

    private void buildRandomAdvertisementList() throws Exception {
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle("셋업 데이터");
        request.setWinningBid(5);
        request.setExposureDate(LocalDateTime.of(2021,6,18,12,00));
        request.setExpiryDate(LocalDateTime.of(2021,6,30,12,00));

        String fileName = "temp2.jpg";
        MultipartFile multipartFile = buildMockMultipartFile(fileName);
        request.setImage(multipartFile);

        for(int i = 0 ; i < 30; ++i) {
            advertisementService.createAdvertisement(request);
        }
    }
}