package com.deali.adtech.infrastructure.util.mapper;

import com.deali.adtech.domain.ExposedLog;
import com.deali.adtech.presentation.dto.ResponseCreative;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExposedLogMapper {
    ExposedLogMapper INSTANCE = Mappers.getMapper(ExposedLogMapper.class);

    ExposedLog toLog(ResponseCreative creative);
}
