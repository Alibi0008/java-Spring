package com.example.practice_3.delivery.infra;

import com.example.practice_3.delivery.web.dto.QuoteRecordDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper: Persistence (QuoteRecord Entity) -> API DTO (QuoteRecordDto).
 * Реализация генерируется во время компиляции без reflection.
 */
@Mapper(componentModel = "spring")
public interface QuoteEntityMapper {

    @Mapping(target = "regionCode", source = "region.code")
    @Mapping(target = "regionName", source = "region.name")
    QuoteRecordDto toDto(QuoteRecord entity);

    List<QuoteRecordDto> toDtoList(List<QuoteRecord> entities);
}
