package com.deali.adtech.application;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementExposeCount;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.domain.AdvertisementStatus;
import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;
import com.deali.adtech.infrastructure.exception.InvalidChangeDurationException;
import com.deali.adtech.infrastructure.exception.InvalidTitleException;
import com.deali.adtech.infrastructure.repository.AdvertisementExposeCountRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementMapper;
import com.deali.adtech.infrastructure.util.support.FileUploadSupport;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    @Mock
    private AdvertisementMapper advertisementMapper;
    @Mock
    private FileUploadSupport fileUploadSupport;

    @InjectMocks
    private AdvertisementService advertisementService;

    private Advertisement advertisement;
    private AdvertisementImage image;
    private AdvertisementExposeCount exposeCount;
    private static final String TEST_PATH = "/Users/admin/temp-image/";

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
        request.setImage(mockMultipartFile("image","temp2.jpg"));


        doNothing().when(fileUploadSupport).uploadMultipartFileImage(any(),any());

        given(advertisementRepository.save(any())).willReturn(advertisement);
        given(imageRepository.save(any())).willReturn(image);
        given(exposeCountRepository.save(any())).willReturn(exposeCount);
        given(advertisementMapper.dtoToEntity(any())).willReturn(advertisement);
        given(advertisementMapper.fileToEntity(any(), any())).willReturn(image);


        /* when */
        Long id = advertisementService.createAdvertisement(request);

        /* then */
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 이미지가 없는 경우")
    public void edit_advertisement_success_test_no_image() throws Exception {
        /* given */
        LocalDateTime newExposureDate = LocalDateTime.from(advertisement.getExposureDate())
                .plusDays(30);
        LocalDateTime newExpiryDate = LocalDateTime.from(newExposureDate).plusDays(30);

        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(advertisement.getId());
        request.setTitle("수정 제목");
        request.setWinningBid(1);
        request.setExposureDate(newExposureDate);
        request.setExpiryDate(newExpiryDate);

        given(advertisementRepository.findById(any())).willReturn(Optional.of(advertisement));

        /* when */
        advertisementService.editAdvertisement(request);

        /* then */
        assertThat(advertisement)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", request.getWinningBid())
                .hasFieldOrPropertyWithValue("exposureDate", request.getExposureDate())
                .hasFieldOrPropertyWithValue("expiryDate", request.getExpiryDate())
                .hasFieldOrPropertyWithValue("status", AdvertisementStatus.WAITING);
    }

    @Test
    @DisplayName("소재 수정 성공 테스트 교체할 이미지가 있는 경우")
    public void edit_advertisement_success_test_with_image() throws Exception {
        /* given */
        LocalDateTime newExposureDate = LocalDateTime.from(advertisement.getExposureDate())
                .plusDays(30);
        LocalDateTime newExpiryDate = LocalDateTime.from(newExposureDate).plusDays(30);

        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(advertisement.getId());
        request.setTitle("수정된 제목");
        request.setWinningBid(9);
        request.setExposureDate(newExposureDate);
        request.setExpiryDate(newExpiryDate);
        request.setNewImage(mockMultipartFile("newImage", "temp2.jpg"));

        //TODO:: 이미지 업로드 로직 변경후에 다시 테스트
        doNothing().when(fileUploadSupport).exchangeMultipartFileImage(any(),any(),any());

        given(advertisementRepository.findById(any()))
                .willReturn(Optional.of(advertisement));
        given(imageRepository.findByAdvertisementId(any()))
                .willReturn(Arrays.asList(image));

        /* when */
        advertisementService.editAdvertisement(request);

        /* then */

        assertThat(advertisement)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", request.getWinningBid())
                .hasFieldOrPropertyWithValue("exposureDate", request.getExposureDate())
                .hasFieldOrPropertyWithValue("expiryDate", request.getExpiryDate());
    }

    @Test
    @DisplayName("소재 수정 실패 테스트 소재가 삭제 상태인 경우")
    public void edit_advertisement_fail_test_already_removed() throws Exception {
        /* given */
        LocalDateTime newExposureDate = LocalDateTime.from(advertisement.getExposureDate())
                .plusDays(30);
        LocalDateTime newExpiryDate = LocalDateTime.from(newExposureDate).plusDays(30);

        RequestEditAdvertisement request = new RequestEditAdvertisement();
        request.setId(advertisement.getId());
        request.setTitle("수정된 제목");
        request.setWinningBid(9);
        request.setExposureDate(newExposureDate);
        request.setExpiryDate(newExpiryDate);
        request.setNewImage(mockMultipartFile("newImage", "temp2.jpg"));

        advertisement.remove();

        given(advertisementRepository.findById(any()))
                .willReturn(Optional.of(advertisement));

        /* when */

        assertThatExceptionOfType(AlreadyRemovedAdvertisementException.class)
                .isThrownBy(() -> {
                    advertisementService.editAdvertisement(request);
                });

        /* then */
    }

   @Test
   @DisplayName("소재 수정 실패 테스트 소재의 새로운 노출 만료 기간이 노출 시작 기간보다 이전인 경우")
   public void edit_advertisement_fail_test_invalid_expiry_date() throws Exception {
       /* given */
       LocalDateTime newExposureDate = LocalDateTime.from(advertisement.getExposureDate())
               .plusDays(30);
       LocalDateTime newExpiryDate = LocalDateTime.from(newExposureDate).minusDays(30);

       RequestEditAdvertisement request = new RequestEditAdvertisement();
       request.setId(advertisement.getId());
       request.setTitle("수정된 제목");
       request.setWinningBid(9);
       request.setExposureDate(newExposureDate);
       request.setExpiryDate(newExpiryDate);
       request.setNewImage(mockMultipartFile("newImage", "temp2.jpg"));

       given(advertisementRepository.findById(any()))
               .willReturn(Optional.of(advertisement));

       /* when */
       assertThatExceptionOfType(InvalidChangeDurationException.class)
               .isThrownBy(()->{
                  advertisementService.editAdvertisement(request);
               });

       /* then */

   }

   @Test
   @DisplayName("소재 수정 실패 테스트 소재의 만료기간이 현재 시간보다 이전인 경우")
   public void edit_advertisement_fail_test_expiry_date_is_before_current_time() throws Exception {
       /* given */
       LocalDateTime newExposureDate = LocalDateTime.from(advertisement.getExposureDate())
               .plusDays(30);
       LocalDateTime newExpiryDate = LocalDateTime.now().minusDays(30);

       RequestEditAdvertisement request = new RequestEditAdvertisement();
       request.setId(advertisement.getId());
       request.setTitle("수정된 제목");
       request.setWinningBid(9);
       request.setExposureDate(newExposureDate);
       request.setExpiryDate(newExpiryDate);
       request.setNewImage(mockMultipartFile("newImage", "temp2.jpg"));

       given(advertisementRepository.findById(any()))
               .willReturn(Optional.of(advertisement));

       /* when */
       assertThatExceptionOfType(InvalidChangeDurationException.class)
               .isThrownBy(()->{
                   advertisementService.editAdvertisement(request);
               });

       /* then */
   }

   @Test
   @DisplayName("소재 수정 실패 테스트 소재의 수정하려는 광고 시작 기간이 현재 광고 시작 기간보다 이전인 경우")
   public void edit_advertisement_fail_test_invalid_exposure_date() throws Exception {
       /* given */
       LocalDateTime newExposureDate = LocalDateTime.from(advertisement.getExposureDate())
               .minusDays(30);
       LocalDateTime newExpiryDate = LocalDateTime.from(advertisement.getExpiryDate())
               .plusDays(30);

       RequestEditAdvertisement request = new RequestEditAdvertisement();
       request.setId(advertisement.getId());
       request.setTitle("수정된 제목");
       request.setWinningBid(9);
       request.setExposureDate(newExposureDate);
       request.setExpiryDate(newExpiryDate);
       request.setNewImage(mockMultipartFile("newImage", "temp2.jpg"));

       given(advertisementRepository.findById(any()))
               .willReturn(Optional.of(advertisement));

       /* when */
       assertThatExceptionOfType(InvalidChangeDurationException.class)
               .isThrownBy(()->{
                   advertisementService.editAdvertisement(request);
               });

       /* then */
   }

    @Test
    @DisplayName("소재 삭제 성공 테스트")
    public void remove_advertisement_success_test() throws Exception {
        /* given */
        given(advertisementRepository.findById(any()))
                .willReturn(Optional.of(advertisement));

        /* when */
        advertisementService.removeAdvertisement(advertisement.getId());

        /* then */
        assertThat(advertisement.getStatus())
                .isEqualTo(AdvertisementStatus.DELETED);
    }

    private MockMultipartFile mockMultipartFile(String name, String originFileName) {
        MockMultipartFile file = null;
        File image = new File(TEST_PATH + originFileName);

        try(FileInputStream fileInputStream =
                    new FileInputStream(image)) {
            file = new MockMultipartFile(name, originFileName, "multipart/form-data", fileInputStream);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return file;
    }
}
