package com.deali.adtech.infrastructure.util;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper
public interface AdvertisementMapper {
    AdvertisementMapper INSTANCE = Mappers.getMapper(AdvertisementMapper.class);

    Advertisement dtoToEntity(RequestCreateAdvertisement requestCreateAdvertisement);

    @Mappings({
            @Mapping(source = "path", target = "path"),
            @Mapping(source = "file.contentType", target = "extension")
    })
    AdvertisementImage fileToEntity(MultipartFile file, String path);
}
