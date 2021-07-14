package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.infrastructure.exception.EmptyPoolException;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementDocumentMapper;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MongoAdvertisementDocumentRepository implements AdvertisementDocumentRepository {
    private final MongoTemplate mongoTemplate;
    private final AdvertisementDocumentMapper documentMapper;

    @Value("${pool.bid-rate}")
    private Double bidRate;
    @Value("${pool.date-rate}")
    private Double dateRate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Value("${pool.reference-date}")
    private LocalDateTime referenceDate;

    @Override
    public List<ResponseCreative> searchTop10Advertisement() {
        List<AdvertisementDocument> results = mongoTemplate.findAll(AdvertisementDocument.class);

        if(results == null || results.size() == 0) {
           return new ArrayList<ResponseCreative>();
        }

        Map<String, Number> map = getMinMaxWinningBidAndModifiedAt(results);

        List<ResponseCreative> responseCreativeList =
                results.stream()
                .map(document -> {
                    int bid = document.getWinningBid();
                    LocalDateTime time = document.getModifiedAt();
                    double score = calculateScore(bid, time, map);
                    return documentMapper.documentToDto(document, score);
                })
                        .sorted((d1,d2)->d2.getScore().compareTo(d1.getScore()))
                        .limit(10)
                        .collect(Collectors.toList());

        return responseCreativeList;
    }

    @Override
    public void remove(AdvertisementDocument document) {
        Criteria criteria
                = new Criteria("advertisementId").is(document.getAdvertisementId());

        Query query = new Query();
        query.addCriteria(criteria);

        mongoTemplate.remove(query, AdvertisementDocument.class,"advertisement");
    }

    @Override
    public void update(AdvertisementDocument document) {
        Criteria criteria =
                new Criteria("advertisementId").is(document.getAdvertisementId());

        Query query = new Query();
        query.addCriteria(criteria);

        Update update = new Update();

        update.set("title", document.getTitle());
        update.set("winningBid", document.getWinningBid());
        update.set("modifiedAt", document.getModifiedAt());
        update.set("expiryDate", document.getExpiryDate());
        update.set("imagePath", document.getImagePath());

        FindAndModifyOptions options = new FindAndModifyOptions().upsert(false);

        mongoTemplate.findAndModify(query, update, options, AdvertisementDocument.class);
    }

    //TODO::딱 strategy 패턴인데?
    private double calculateScore(int bid, LocalDateTime time, Map<String, Number> map) {
        Integer minBid = (Integer)map.get("minBid");
        Integer maxBid = (Integer)map.get("maxBid");
        Long minDate = (Long)map.get("minDate");
        Long maxDate = (Long)map.get("maxDate");

        long referenceTime = referenceDate.atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        long convertedTime = time.atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        convertedTime -= referenceTime;

        double bidScore = 0, dateScore = 0;

        if(maxBid-minBid != 0) {
            bidScore = ((bid-minBid)/(double)(maxBid-minBid))*bidRate;
        }

        if(maxDate-minDate != 0) {
            dateScore = ((convertedTime-minDate)/(double)(maxDate-minDate))*dateRate;
        }

        return bidScore+dateScore;
    }

    private Map<String, Number> getMinMaxWinningBidAndModifiedAt(List<AdvertisementDocument> advertisements) {
        Map<String, Number> resultMap = new HashMap<>();

        Integer minBid = Integer.MAX_VALUE, maxBid = Integer.MIN_VALUE;
        Long minDate = Long.MAX_VALUE, maxDate = Long.MIN_VALUE;
        long referenceTime = referenceDate.atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();

        for(AdvertisementDocument advertisement : advertisements) {
            int currentBid = advertisement.getWinningBid();
            long currentDate = advertisement.getModifiedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
            currentDate -= referenceTime;

            if(minBid > currentBid) minBid = advertisement.getWinningBid();
            if(maxBid < currentBid) maxBid = advertisement.getWinningBid();
            if(minDate > currentDate) minDate = currentDate;
            if(maxDate < currentDate) maxDate = currentDate;
        }

        resultMap.put("minBid", minBid);
        resultMap.put("maxBid", maxBid);
        resultMap.put("minDate", minDate);
        resultMap.put("maxDate", maxDate);

        return resultMap;
    }
}
