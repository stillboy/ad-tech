package com.deali.adtech.infrastructure.util.mapper;

import com.deali.adtech.domain.Advertisement;
import com.deali.adtech.domain.AdvertisementImage;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.ResponseCreateAdvertisement;
import com.deali.adtech.presentation.dto.ResponseMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper
public interface AdvertisementMapper {
    AdvertisementMapper INSTANCE = Mappers.getMapper(AdvertisementMapper.class);

    Advertisement dtoToEntity(RequestCreateAdvertisement requestCreateAdvertisement);

    @Mappings
    ({
            @Mapping(source = "path", target = "path"),
            @Mapping(source = "file.originalFilename", target = "name")
    })
    AdvertisementImage fileToEntity(MultipartFile file, String path);

    @Mappings({
            @Mapping(source = "advertisementId", target = "advertisementId"),
            @Mapping(source = "message.message", target = "message")
    })
    ResponseCreateAdvertisement toCreatedResponse(Long advertisementId, ResponseMessage message);
}
