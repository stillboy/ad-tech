package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.presentation.dto.ResponseCreative;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MongoAdvertisementDocumentRepositoryTest {
    @Autowired
    private MongoAdvertisementDocumentRepository advertisementDocumentRepository;

    @Test
    @DisplayName("광고 소재 풀 노출될 상위 10개의 소재 조회 테스트")
    public void search_Top_10_advertisement_success_test() {
        /* given */
        List<ResponseCreative> results = advertisementDocumentRepository.searchTop10Advertisement();

        ResponseCreative max = results.get(0);
        ResponseCreative min = results.get(results.size()-1);

        /* when */
        for(ResponseCreative creative : results) {
            System.out.println(creative);
        }


        /* then */
        assertThat(results.size())
                .isEqualTo(10);

        assertThat(min.getScore())
                .isLessThan(max.getScore());
    }
}