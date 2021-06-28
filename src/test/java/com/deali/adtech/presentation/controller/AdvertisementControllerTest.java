package com.deali.adtech.presentation.controller;

import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AdvertisementControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final static String DEFAULT_PATH = "/core/v1/creative";
    private static final String TEST_PATH = "/Users/admin/temp-image/";

    @BeforeEach
    public void setUp() {
        /* given */

        /* when */

        /* then */
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
        params.add("winningBid", "11");
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
                .andExpect(model().attribute("errors", IsCollectionWithSize.hasSize(2)));
        /* then */
    }

    @Test
    @DisplayName("post advertisement 실패 테스트 exposure date 가 현재 시간 보다 이전일 경우")
    public void create_advertisement_fail_test_wrong_exposure_date()
            throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime exposureDate = LocalDateTime.of(2021,12,5,12,0);

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
                .andExpect(status().is3xxRedirection());
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