package com.deali.adtech.application;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementExposeCount;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.infrastructure.repository.AdvertisementExposeCountRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceUnitTest {
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private AdvertisementImageRepository imageRepository;
    @Mock
    private AdvertisementExposeCountRepository exposeCountRepository;

    @InjectMocks
    private AdvertisementService advertisementService;

    private Advertisement advertisement;
    private AdvertisementImage image;
    private AdvertisementExposeCount exposeCount;

    @BeforeEach
    public void setUp() {
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        advertisement = Advertisement.builder()
                .title("테스트 광고")
                .winningBid(10)
                .exposureDate(exposureDate)
                .expiryDate(expiryDate)
                .build();

        ReflectionTestUtils.setField(advertisement, "id", 1L);

        image = mock(AdvertisementImage.class);

        ReflectionTestUtils.setField(image, "advertisement", advertisement);
        ReflectionTestUtils.setField(image, "name", "temp2");
        ReflectionTestUtils.setField(image, "extension", "jpg");
        ReflectionTestUtils.setField(image, "path", "/User/admin/temp-image");
        ReflectionTestUtils.setField(image, "size", 100L);

        exposeCount = AdvertisementExposeCount.builder()
                .advertisement(advertisement)
                .build();
    }

    @Test
    @DisplayName("소재 생성 성공 테스트")
    public void create_advertisement_success_test() throws Exception {
        /* given */
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setTitle(advertisement.getTitle());
        request.setWinningBid(advertisement.getWinningBid());
        request.setExposureDate(advertisement.getExposureDate());
        request.setExpiryDate(advertisement.getExpiryDate());
        request.setImage(null);

        given(advertisementRepository.save(any())).willReturn(advertisement);
        given(imageRepository.save(any())).willReturn(image);
        given(exposeCountRepository.save(any())).willReturn(exposeCount);

        doNothing().when(image).bindAdvertisement(any());
        doNothing().when(image).uploadImageFile(any());

        /* when */
        Long id = advertisementService.createAdvertisement(request);

        /* then */
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 이미지가 없는 경우")
    public void edit_advertisement_success_test_no_image() throws Exception {
        /* given */
        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(advertisement.getId());
        request.setTitle(advertisement.getTitle());
        request.setWinningBid(1);
        request.setExposureDate(advertisement.getExposureDate());
        request.setExpiryDate(advertisement.getExpiryDate());

        given(advertisementRepository.findById(any())).willReturn(Optional.of(advertisement));

        /* when */
        advertisementService.editAdvertisement(request);

        /* then */
        verify(advertisementService, times(1)).editAdvertisement(any());
    }
}
