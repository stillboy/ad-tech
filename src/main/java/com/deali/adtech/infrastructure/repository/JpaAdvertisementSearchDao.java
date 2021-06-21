package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.presentation.dto.AdvertisementSearchCondition;
import com.deali.adtech.presentation.dto.QResponseAdvertisement;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.deali.adtech.domain.QAdvertisement.*;
import static com.deali.adtech.domain.QExposeCount.*;

@RequiredArgsConstructor
@Repository
public class JpaAdvertisementSearchDao implements AdvertisementSearchDao {
    private final JPAQueryFactory queryFactory;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementImageRepository advertisementImageRepository;

    @Override
    public Page<ResponseAdvertisement> searchAdvertisement(Pageable pageable, AdvertisementSearchCondition searchCondition) {
        return null;
    }

    @Override
    public ResponseAdvertisement findAdvertisementById(Long advertisementId) {
        //TODO::redis와 연동해서 조회수를 redis에서 가져오는 로직 추가
        ResponseAdvertisement responseAdvertisement = queryFactory
                .select(new QResponseAdvertisement(advertisement.id, advertisement.title,
                        advertisement.winningBid, advertisement.createdAt, advertisement.modifiedAt,
                        advertisement.exposureDate, advertisement.expiryDate, advertisement.status,
                        exposeCount1.exposeCount))
                .from(advertisement)
                .leftJoin(exposeCount1)
                .on(advertisement.id.eq(exposeCount1.advertisement.id))
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
