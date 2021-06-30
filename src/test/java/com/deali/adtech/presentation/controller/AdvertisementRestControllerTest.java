package com.deali.adtech.presentation.controller;

import com.deali.adtech.RequestCreateAdvertisementDeserializer;
import com.deali.adtech.infrastructure.repository.AdvertisementImageRepository;
import com.deali.adtech.infrastructure.repository.AdvertisementRepository;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc()
@AutoConfigureJsonTesters
@SpringBootTest
public class AdvertisementRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final static String DEFAULT_PATH = "/core/v1/creative";
    private static final String TEST_PATH = "/Users/admin/temp-image/";
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private AdvertisementImageRepository imageRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private Long targetId;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JacksonTester<RequestCreateAdvertisement> jsonCreateRequest;

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
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .param("exposureDate",request.getExposureDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
                .andReturn()
                .getResponse();

        /* then */
        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CREATED.value());
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
