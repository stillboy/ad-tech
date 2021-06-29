package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementDocumentMapper;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MongoAdvertisementDocumentRepository
        implements AdvertisementDocumentRepository {
    private final MongoTemplate mongoTemplate;

    @Value("${pool.bid-rate}")
    private Double bidRate;
    @Value("${pool.date-rate}")
    private Double dateRate;

    @Override
    public List<ResponseCreative> searchTop10Advertisement() {
        List<AdvertisementDocument> results = mongoTemplate.findAll(AdvertisementDocument.class);

        Map<String, Number> map = getMinMaxWinningBidAndModifiedAt(results);

        List<ResponseCreative> responseCreativeList =
                results.stream()
                .map(document -> {
                    int bid = document.getWinningBid();
                    LocalDateTime time = document.getModifiedAt();
                    double score = calculateScore(bid, time, map);
                    return AdvertisementDocumentMapper.INSTANCE.documentToDto(document, score);
                })
                        .sorted((d1,d2)->d2.getScore().compareTo(d1.getScore()))
                        .limit(10)
                        .collect(Collectors.toList());

        return responseCreativeList;
    }

    private Double calculateScore(int bid, LocalDateTime time, Map<String, Number> map) {
        Integer minBid = (Integer)map.get("minBid");
        Integer maxBid = (Integer)map.get("maxBid");
        Long minDate = (Long)map.get("minDate");
        Long maxDate = (Long)map.get("maxDate");

        long convertedTime = time.atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()*1000;

        return ((bid-minBid)/(double)(maxBid-minBid))*bidRate
                + ((convertedTime-minDate)/(double)(maxDate-minDate))*dateRate;
    }

    private Map<String, Number> getMinMaxWinningBidAndModifiedAt(List<AdvertisementDocument> advertisements) {
        //TODO::익셉션 정의 필요
        if(advertisements == null || advertisements.size() == 0) {
            throw new RuntimeException();
        }

        Map<String, Number> resultMap = new HashMap<>();

        Integer minBid = Integer.MAX_VALUE, maxBid = Integer.MIN_VALUE;
        Long minDate = Long.MAX_VALUE, maxDate = Long.MIN_VALUE;

        for(AdvertisementDocument advertisement : advertisements) {
            int currentBid = advertisement.getWinningBid();
            long currentDate = advertisement.getModifiedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();

            if(minBid > currentBid) minBid = advertisement.getWinningBid();
            if(maxBid < currentBid) maxBid = advertisement.getWinningBid();
            if(minDate > currentDate) minDate = currentDate;
            if(maxDate < currentDate) maxDate = currentDate;
        }

        minDate *= 1000; maxDate *= 1000;

        resultMap.put("minBid", minBid);
        resultMap.put("maxBid", maxBid);
        resultMap.put("minDate", minDate);
        resultMap.put("maxDate", maxDate);

        return resultMap;
    }
}
