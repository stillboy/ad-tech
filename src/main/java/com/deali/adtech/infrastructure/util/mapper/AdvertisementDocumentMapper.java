package com.deali.adtech.infrastructure.util.mapper;

import com.deali.adtech.domain.AdvertisementDocument;
import com.deali.adtech.presentation.dto.ResponseCreative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdvertisementDocumentMapper {
    AdvertisementDocumentMapper INSTANCE = Mappers.getMapper(AdvertisementDocumentMapper.class);

    public ResponseCreative documentToDto(AdvertisementDocument document, Double score);
}
