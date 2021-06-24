package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.domain.QAdvertisementExposeCount;
import com.deali.adtech.presentation.dto.AdvertisementSearchCondition;
import com.deali.adtech.presentation.dto.QResponseAdvertisement;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.deali.adtech.domain.QAdvertisement.*;
import static com.deali.adtech.domain.QAdvertisementExposeCount.*;

@RequiredArgsConstructor
@Repository
public class JpaAdvertisementSearchDao implements AdvertisementSearchDao {
    private final JPAQueryFactory queryFactory;
    private final AdvertisementImageRepository advertisementImageRepository;

    @Override
    public Page<ResponseAdvertisement> searchAdvertisement(Pageable pageable, AdvertisementSearchCondition searchCondition) {
        //TODO::소팅, 검색조건 생각
        QueryResults<ResponseAdvertisement> result = queryFactory
                .select(new QResponseAdvertisement(advertisement.id, advertisement.title,
                        advertisement.winningBid, advertisement.createdAt, advertisement.modifiedAt,
                        advertisement.exposureDate, advertisement.expiryDate, advertisement.status))
                .from(advertisement)
                .orderBy(advertisement.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<ResponseAdvertisement>(result.getResults(), pageable, result.getTotal());
    }

    //TODO:: 레디스 db 연동 작업, 일단 10분 마다
    @Override
    public ResponseAdvertisement findAdvertisementById(Long advertisementId) {
        ResponseAdvertisement responseAdvertisement = queryFactory
                .select(new QResponseAdvertisement(advertisement.id, advertisement.title,
                        advertisement.winningBid, advertisement.createdAt, advertisement.modifiedAt,
                        advertisement.exposureDate, advertisement.expiryDate, advertisement.status,
                        advertisementExposeCount.exposeCount))
                .from(advertisement)
                .leftJoin(advertisementExposeCount)
                .on(advertisement.id.eq(advertisementExposeCount.advertisement.id))
                .where(advertisement.id.eq(advertisementId))
                .fetchOne();

        List<AdvertisementImage> images = advertisementImageRepository.findByAdvertisementId(advertisementId);
        List<String> imagePathList = new ArrayList<>();

        images.forEach(image -> {
            imagePathList.add(image.getFullPathName());
        });

        responseAdvertisement.setImages(imagePathList);

        return responseAdvertisement;
    }
}
