package com.deali.adtech.presentation.controller;

import com.deali.adtech.presentation.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@SpringBootTest
public class AdvertisementRestControllerTest {
    private final static String DEFAULT_PATH = "/core/v1/creative";
    private static final String TEST_PATH = "/Users/admin/temp-image/";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;

    private MockHttpServletResponse createResponse;
    private RequestCreateAdvertisement requestCreateAdvertisement;
    private Long targetId;

    @BeforeEach
    public void setUp() throws Exception {
        /* given */
        requestCreateAdvertisement = mockRequestCreateAdvertisement();
        MockMultipartFile file = mockMultipartFile("image", "temp2.jpg");

        /* when */
        createResponse = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(DEFAULT_PATH)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", requestCreateAdvertisement.getTitle())
                        .param("winningBid", requestCreateAdvertisement.getWinningBid().toString())
                        .param("expiryDate",requestCreateAdvertisement.getExpiryDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
                        .param("exposureDate",requestCreateAdvertisement.getExposureDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andReturn()
                .getResponse();

        ResponseCreateAdvertisement responseCreateAdvertisement
                = objectMapper.readValue(createResponse.getContentAsString(),
                ResponseCreateAdvertisement.class);

        targetId = responseCreateAdvertisement.getAdvertisementId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("createAdvertisement 성공 테스트")
    public void create_advertisement_success_test() throws Exception {
        /* given */
        RequestCreateAdvertisement request = mockRequestCreateAdvertisement();
        MockMultipartFile file = mockMultipartFile("image", "temp2.jpg");

        /* when */
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(DEFAULT_PATH)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", request.getTitle())
                .param("winningBid", request.getWinningBid().toString())
                .param("expiryDate",request.getExpiryDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
                .param("exposureDate",request.getExposureDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andReturn()
                .getResponse();

        ResponseCreateAdvertisement responseCreateAdvertisement
                = objectMapper.readValue(response.getContentAsString(),
                ResponseCreateAdvertisement.class);
        /* then */
        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CREATED.value());

        assertThat(responseCreateAdvertisement.getMessage())
                .isEqualTo(ResponseMessage.ADVERTISEMENT_CREATED.getMessage());
    }

    @Test
    @DisplayName("editAdvertisement 성공 테스트 이미지가 없는 경우")
    public void edit_advertisement_success_test() throws Exception {
        RequestEditAdvertisement editRequest = mockRequestEditAdvertisement();

        /* when */
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(DEFAULT_PATH+"/"+targetId.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("title", editRequest.getTitle())
                        .param("winningBid", editRequest.getWinningBid().toString())
                        .param("exposureDate", editRequest.getExposureDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
                        .param("expiryDate", editRequest.getExpiryDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andReturn()
                .getResponse();

        /* then */
        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("editAdvertisement 성공 테스트 이미지가 있는 경우")
    public void edit_advertisement_success_test_with_image() throws Exception {
        /* given */
        RequestEditAdvertisement editRequest = mockRequestEditAdvertisement();
        MockMultipartFile file = mockMultipartFile("newImage", "temp2.jpg");

        /* when */
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(DEFAULT_PATH+"/"+targetId.toString())
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("title", editRequest.getTitle())
                        .param("winningBid", editRequest.getWinningBid().toString())
                        .param("exposureDate", editRequest.getExposureDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
                        .param("expiryDate", editRequest.getExpiryDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andReturn()
                .getResponse();

        /* then */
        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.OK.value());


    }

    @Test
    @DisplayName("deleteAdvertisement 성공 테스트")
    public void delete_advertisement_success_test() throws Exception {
        /* given */

        /* when */

        MockHttpServletResponse advertisementDeletedResponse = mockMvc.perform(
                MockMvcRequestBuilders.delete(DEFAULT_PATH+"/" +targetId))
                .andReturn()
                .getResponse();


        ResponseDeleteAdvertisement responseDeleteAdvertisement
                = objectMapper.readValue(advertisementDeletedResponse.getContentAsString(),
                ResponseDeleteAdvertisement.class);
        /* then */
        assertThat(advertisementDeletedResponse.getStatus())
                .isEqualTo(HttpStatus.OK.value());

        assertThat(responseDeleteAdvertisement.getMessage())
                .isEqualTo(ResponseMessage.ADVERTISEMENT_DELETE.getMessage());
    }

    @Test
    @DisplayName("getAdvertisement 성공 테스트")
    public void get_advertisement_success_test() throws Exception {
        /* given */

        /* when */

        MockHttpServletResponse getResponse = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(DEFAULT_PATH+"/" +targetId))
                .andReturn()
                .getResponse();

        ResponseAdvertisement responseAdvertisement
                = objectMapper.readValue(getResponse.getContentAsString(),
                ResponseAdvertisement.class);

        /* then */
        assertThat(getResponse.getStatus())
                .isEqualTo(HttpStatus.OK.value());

        assertThat(responseAdvertisement)
                .hasFieldOrPropertyWithValue("title", requestCreateAdvertisement.getTitle())
                .hasFieldOrPropertyWithValue("winningBid", requestCreateAdvertisement.getWinningBid());

        assertThat(responseAdvertisement.getExpiryDate().toString())
                .isEqualTo(requestCreateAdvertisement.getExpiryDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));

        assertThat(responseAdvertisement.getExposureDate().toString())
                .isEqualTo(requestCreateAdvertisement.getExposureDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    }

    @Test
    @DisplayName("getAdvertisementList 성공 테스트")
    public void get_advertisement_list_success_test() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .get(DEFAULT_PATH)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        /* then */

    }

    private RequestCreateAdvertisement mockRequestCreateAdvertisement() {
        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        LocalDateTime exposureDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        request.setExposureDate(exposureDate);
        request.setExpiryDate(expiryDate);
        request.setTitle("테스트 광고");
        request.setWinningBid(10);

        return request;
    }

    private RequestEditAdvertisement mockRequestEditAdvertisement() {
        RequestEditAdvertisement request = new RequestEditAdvertisement();
        LocalDateTime exposureDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(30);
        LocalDateTime expiryDate = LocalDateTime.from(exposureDate).plusDays(30);

        request.setId(targetId);
        request.setTitle("수정 타이틀");
        request.setWinningBid(1);
        request.setExposureDate(exposureDate);
        request.setExpiryDate(expiryDate);

        return request;
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
