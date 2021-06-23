package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.presentation.dto.ResponseCreative;
import org.apache.tomcat.jni.Local;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Divide;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Multiply;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AdvertisementDocumentMongoRepositoryTest {
    @Autowired
    private AdvertisementDocumentRepository advertisementDocumentRepository;

    @Test
    public void search_min_max_winningBid_and_modified_at_success_test() {
        HashMap<String, Number> result =
                advertisementDocumentRepository.searchMinMaxWinningBidAndModifiedAt();
    }

    @Test
    public void test() {
       List<ResponseCreative> results =
               advertisementDocumentRepository.searchTop10Advertisement();
    }
}