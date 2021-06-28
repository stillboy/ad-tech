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
    private AdvertisementDocumentRepository advertisementDocumentRepository;

    @Test
    @DisplayName("광고 소재 풀 낙찰가,수정일자 최대/최소값 조회 테스트")
    public void search_min_max_winningBid_and_modified_at_success_test() {
        /* given */

        /* when */
        HashMap<String, Number> result =
                advertisementDocumentRepository.searchMinMaxWinningBidAndModifiedAt();

        /* then */
        assertThat(result.get("maxBid"))
                .isOfAnyClassIn(Integer.class);
        assertThat(result.get("minBid"))
                .isOfAnyClassIn(Integer.class);
        assertThat(result.get("minDate"))
                .isOfAnyClassIn(Long.class);
        assertThat(result.get("maxDate"))
                .isOfAnyClassIn(Long.class);
    }

    @Test
    @DisplayName("광고 소재 풀 노출될 상위 10개의 소재 조회 테스트")
    public void search_Top_10_advertisement_success_test() {
        /* given */
        double bidRate = 0.6;
        double dateRate = 0.4;
        List<ResponseCreative> results =
                advertisementDocumentRepository.searchTop10Advertisement(bidRate, dateRate);

        ResponseCreative max = results.get(0);
        ResponseCreative min = results.get(results.size()-1);

        /* when */

        /* then */
        assertThat(results.size())
                .isEqualTo(10);

        assertThat(min.getScore())
                .isLessThan(max.getScore());
    }
}