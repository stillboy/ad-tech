package com.deali.adtech.infrastructure.repository;

import org.apache.tomcat.jni.Local;
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

@SpringBootTest
class AdvertisementDocumentMongoRepositoryTest {
    @Autowired
    private AdvertisementDocumentRepository advertisementDocumentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void search_min_max_winningBid_and_modified_at_success_test() {
        HashMap result =
                advertisementDocumentRepository.searchMinMaxWinningBidAndModifiedAt();

        Date maxDate = (Date)result.get("maxDate");

        maxDate.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        System.out.println(maxDate.toString());
    }

    @Test
    public void test() {
       advertisementDocumentRepository.searchTop10Advertisement().forEach(System.out::println);
//        HashMap map =
//                advertisementDocumentRepository.searchMinMaxWinningBidAndModifiedAt();
//
//        Date tempMaxDate = (Date)map.get("maxDate");
//        Date tempMinDate = (Date)map.get("minDate");
//
//        Integer maxBid = (Integer) map.get("maxBid");
//        Integer minBid = (Integer) map.get("minBid");
//
//        Long maxDate = tempMaxDate.getTime();
//        Long minDate = tempMinDate.getTime();
//
//        ProjectionOperation projectionOperation;
//        projectionOperation = Aggregation
//                .project("_id","title","winningBid","modifiedAt","expiryDate",
//                        "advertisementId","imagePath")
//                .and(
//                        ArithmeticOperators.Add.valueOf(Multiply.valueOf(6).multiplyBy(Divide
//                                .valueOf(ArithmeticOperators.Subtract.valueOf("winningBid").subtract(minBid))
//                                .divideBy(ArithmeticOperators.Subtract.valueOf(maxBid).subtract(minBid))))
//                        .add(
//                        Multiply.valueOf(4).multiplyBy(Divide
//                                .valueOf(ArithmeticOperators.Subtract
//                                        .valueOf(ConvertOperators.Convert.convertValueOf("modifiedAt").to(18))
//                                        .subtract(minDate))
//                                .divideBy(ArithmeticOperators.Subtract.valueOf(maxDate).subtract(minDate)))
//                        )
//                )
//                .as("score");
//
//        //TODO:: sort 옵션, limit 옵션 분리하기
//
//        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "score");
//
//        LimitOperation limitOperation = Aggregation.limit(10);
//
//        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, sortOperation,
//                limitOperation);
//
//        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "advertisement",
//                HashMap.class);
//
//        results.getMappedResults().forEach(System.out::println);
    }
}