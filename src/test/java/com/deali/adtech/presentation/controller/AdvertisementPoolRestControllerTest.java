package com.deali.adtech.presentation.controller;

import com.deali.adtech.application.AdvertisementPoolService;
import com.deali.adtech.domain.AdvertisementDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class AdvertisementPoolRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdvertisementPoolService advertisementPoolService;

    private static final String DEFAULT_PATH = "/dsp/v1/creative";

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new AdvertisementPoolRestController(advertisementPoolService))
                .build();

        if(testInfo.getDisplayName().equals("상위 10개 소재 노출 실패 테스트 소재 풀에 소재가 하나도 없는 경우")) {
            return ;
        }

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        for(int i = 0; i < 50; ++i) {
            AdvertisementDocument document = AdvertisementDocument.builder()
                    .title(randomString())
                    .winningBid(randInteger())
                    .expiryDate(expiryDate)
                    .modifiedAt(LocalDateTime.now())
                    .advertisementId((long)(i+1))
                    .imagePath("temp2.jpg")
                    .build();

            mongoTemplate.save(document, "advertisement");
        }
    }

    @Test
    @DisplayName("상위 10개 소재 노출 성공 테스트")
    public void get_top_10_advertisement_success_test() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .get(DEFAULT_PATH))
                .andExpect(status().isOk());
        /* then */
    }

    @Test
    @DisplayName("상위 10개 소재 노출 실패 테스트 소재 풀에 소재가 하나도 없는 경우")
    public void get_top_10_advertisement_fail_test_empty_pool() throws Exception {
        /* given */

        /* when */
        mockMvc.perform(MockMvcRequestBuilders
                .get(DEFAULT_PATH))
                .andExpect(status().isOk());
        /* then */
    }

    private String randomString() {
        Random random = new Random();
        return  random.ints(48, 122+1)
                .filter(i -> (i <= 57 || i >= 65) && (i<=90 || i>=97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private int randInteger() {
        return new Random().nextInt(10)+1;
    }
}
