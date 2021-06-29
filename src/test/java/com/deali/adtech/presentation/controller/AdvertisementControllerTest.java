package com.deali.adtech.presentation.controller;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.infrastructure.exception.ImageUploadFailureException;
import com.deali.adtech.infrastructure.exception.InvalidExpiryDateException;
import com.deali.adtech.infrastructure.exception.InvalidExposureDateException;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.beans.HasPropertyWithValue.*;
import static org.hamcrest.core.IsEqual.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AdvertisementControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final static String DEFAULT_PATH = "/core/v1/creative";
    private static final String TEST_PATH = "/Users/admin/temp-image/";
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private AdvertisementImageRepository imageRepository;
    private Long targetId;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        Advertisement advertisement = Advertisement.builder()
                .title("title1")
                .winningBid(5)
                .expiryDate(expiryDate)
                .exposureDate(exposureDate)
                .build();

        AdvertisementImage advertisementImage = AdvertisementImage.builder()
                .name("temp2.jpg")
                .path(TEST_PATH)
                .size(1000L)
                .build();

        advertisementImage.bindAdvertisement(advertisement);

        /* when */

        advertisement = advertisementRepository.save(advertisement);

        /* then */
        this.targetId = advertisement.getId();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("home 성공 테스트")
    public void home_success_test() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(
                MockMvcRequestBuilders.get(DEFAULT_PATH)
                        .accept("application/html;charset=UTF-8"))
                .andExpect(view().name("home"));

        /* then */
    }

    @Test
    @DisplayName("post advertisement View 성공 테스트")
    public void create_advertisement_view_success_test() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(
                MockMvcRequestBuilders.get(DEFAULT_PATH+"/post")
                .accept("application/html;charset=UTF-8"))
                .andExpect(view().name("creativeForm"));

        /* then */
    }


    @Test
    @DisplayName("post advertisement 성공 테스트")
    public void create_advertisement_success_test() throws Exception {
        /* given */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);

        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "test-title");
        params.add("winningBid", "10");
        params.add("exposureDate", exposureDate.format(formatter));
        params.add("expiryDate", expiryDate.format(formatter));

        MockPart part = new MockPart("image",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().is3xxRedirection());
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 광고 제목, 낙찰가의 형식이 맞지 않는 경우")
    public void create_advertisement_fail_test_wrong_format_title_and_winning_bid()
            throws Exception {
        /* given */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);

        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "t");
        params.add("winningBid", "15");
        params.add("exposureDate", exposureDate.format(formatter));
        params.add("expiryDate", expiryDate.format(formatter));

        MockPart part = new MockPart("image",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("home"))
                .andExpect(result -> assertThat(result.getResolvedException())
                .isOfAnyClassIn(BindException.class))
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(2)));
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 날짜 형식이 맞지 않는 경우")
    public void create_advertisement_fail_test_wrong_format_date_time()
            throws Exception{
        /* given */
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "title");
        params.add("winningBid", "10");
        params.add("exposureDate", exposureDate.toString());
        params.add("expiryDate", expiryDate.toString());

        MockPart part = new MockPart("image",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("home"))
                .andExpect(result -> assertThat(result.getResolvedException())
                .isOfAnyClassIn(BindException.class))
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(2)));
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 exposure date 가 현재 시간 보다 이전일 경우")
    public void create_advertisement_fail_test_wrong_exposure_date()
            throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.of(1994,11,20,12,0);

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "test-title");
        params.add("winningBid", "10");
        params.add("exposureDate", exposureDate.format(formatter));
        params.add("expiryDate", expiryDate.format(formatter));

        MockPart part = new MockPart("image",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                .isOfAnyClassIn(InvalidExposureDateException.class))
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(1)));
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 expiry date 가 광고 시작 일자보다 이전일 경우")
    public void create_advertisement_fail_test_wrong_expiry_date()
            throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);

        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).minusDays(1);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "test-title");
        params.add("winningBid", "10");
        params.add("exposureDate", exposureDate.format(formatter));
        params.add("expiryDate", expiryDate.format(formatter));

        MockPart part = new MockPart("image",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("home"))
                .andExpect(result -> assertThat(result.getResolvedException())
                .isOfAnyClassIn(InvalidExpiryDateException.class))
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(1)));
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 expiry date 가 현재 시간보다 이전일 경우")
    public void create_advertisement_fail_test_expiry_date_before_current_time()
            throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);

        LocalDateTime expiryDate = LocalDateTime.from(LocalDateTime.now()).minusDays(1);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "test-title");
        params.add("winningBid", "10");
        params.add("exposureDate", exposureDate.format(formatter));
        params.add("expiryDate", expiryDate.format(formatter));

        MockPart part = new MockPart("image",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("home"))
                .andExpect(result -> assertThat(result.getResolvedException())
                .isOfAnyClassIn(InvalidExpiryDateException.class))
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(1)));
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 이미지 업로드에 실패한 경우")
    public void create_advertisement_fail_test_image_upload ()
            throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.now().plusDays(30);

        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        MockMultipartFile image = buildMockMultipartFile("temp2.jpg");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "test-title");
        params.add("winningBid", "10");
        params.add("exposureDate", exposureDate.format(formatter));
        params.add("expiryDate", expiryDate.format(formatter));

        MockPart part = new MockPart("images",
                image.getOriginalFilename(),
                image.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/post")
                .part(part)
                .params(params))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("home"))
                .andExpect(result -> assertThat(result.getResolvedException())
                .isOfAnyClassIn(ImageUploadFailureException.class))
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(1)));
    }

    @Test
    @DisplayName("edit advertisement 성공 테스트 변경할 이미지가 없는 경우")
    public void edit_advertisement_success_test() throws Exception{
        /* given */
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "editedTitle");
        map.add("winningBid", "10");
        map.add("id", targetId.toString());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .post(DEFAULT_PATH+"/update"+"/"+targetId.toString())
                .params(map))
                .andExpect(status().is3xxRedirection());
        /* then */
    }

    @Test
    @Disabled
    @DisplayName("edit advertisement 성공 테스트 변경할 이미지가 있는 경우")
    public void edit_advertisement_success_test_with_image() throws Exception {
        /* given */
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "editedTitle");
        map.add("winningBid", "10");
        map.add("id", targetId.toString());

        MockMultipartFile file = buildMockMultipartFile("temp2.jpg");

        MockPart mockPart = new MockPart("newImage", file.getOriginalFilename(),
                file.getBytes());

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .multipart(DEFAULT_PATH+"/update"+"/"+targetId.toString())
                .part(mockPart)
                .params(map))
                .andExpect(status().isOk());

        /* then */
    }


    @Test
    @DisplayName("delete advertisement 성공 테스트")
    public void delete_advertisement_success_test() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .post(DEFAULT_PATH+"/"+targetId.toString()+"/delete"))
                .andExpect(status().is3xxRedirection());
        /* then */
    }

    @Test
    @DisplayName("get advertisement list 성공 테스트")
    public void get_advertisement_list_success_test() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .get(DEFAULT_PATH+"/list")
                .queryParam("pageNumber", "0")
                .queryParam("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page", "creativeList"));
        /* then */
    }

    @Test
    @DisplayName("get advertisement details 성공 테스트")
    public void get_advertisement_details_success_test() throws Exception {
        /* given */
        Advertisement target = advertisementRepository
                .findById(targetId).orElseThrow(EntityNotFoundException::new);

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .get(DEFAULT_PATH+"/"+targetId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(model().attribute("creative",
                        hasProperty("title", equalTo(target.getTitle()))))
                .andExpect(model().attribute("creative",
                        hasProperty("winningBid", equalTo(target.getWinningBid())))
                );

        /* then */
    }

    private MockMultipartFile buildMockMultipartFile(String fileName) {
        MockMultipartFile file = null;
        File image = new File(TEST_PATH + fileName);

        try(FileInputStream fileInputStream =
                    new FileInputStream(image)) {
            file = new MockMultipartFile(fileName, fileName, "jpg", fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return file;
    }
}