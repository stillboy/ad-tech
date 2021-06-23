package com.deali.adtech.application;

import com.deali.adtech.infrastructure.repository.AdvertisementDocumentMongoRepository;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdvertisementPoolService {
    private final AdvertisementDocumentMongoRepository advertisementDocumentMongoRepository;

    //TODO:: 로깅 AOP로 분리, 하는 김에 이벤트 퍼블리싱도 분리시키기
    public List<ResponseCreative> getTop10Advertisement() {
        List<ResponseCreative> results =
                advertisementDocumentMongoRepository.searchTop10Advertisement();

        results.forEach(creative -> {
           log.info(String
                   .format("소재 노출 식별자: %d 노출 일자: %s 점수 :%f 수정일자: %s",
                   creative.getAdvertisementId(),
                           ZonedDateTime.now(ZoneId.of("Asia/Seoul")),
                           creative.getScore(),
                           creative.getModifiedAt()));
        });

        return results;
    }
}
