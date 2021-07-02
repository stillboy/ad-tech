package com.deali.adtech.infrastructure.util.mapper;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.presentation.dto.ResponseCreative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.deali.adtech.domain.AdvertisementDocument.*;

@Mapper(componentModel = "spring")
public interface AdvertisementDocumentMapper {
    AdvertisementDocumentMapper INSTANCE = Mappers.getMapper(AdvertisementDocumentMapper.class);

    public ResponseCreative documentToDto(AdvertisementDocument document, Double score);

    default AdvertisementDocument entityToDocument(Advertisement advertisement) {
        AdvertisementDocumentBuilder builder = AdvertisementDocument.builder();

        if(advertisement != null ) {
            builder.advertisementId(advertisement.getId());
            builder.modifiedAt(advertisement.getModifiedAt());
            builder.expiryDate(advertisement.getExpiryDate());
            builder.winningBid(advertisement.getWinningBid());
        }

        List<AdvertisementImage> images = advertisement.getImages();

        if(images != null && images.size() >= 1) {
            builder.imagePath(images.get(0).getNameAndExtension());
        } else {
            builder.imagePath("none");
        }

        return builder.build();
    }
}
