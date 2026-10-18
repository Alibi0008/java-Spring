package com.example.practice_3.delivery.web;

import com.example.practice_3.delivery.domain.DeliveryQuote;
import com.example.practice_3.delivery.web.dto.QuoteResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper: Domain (DeliveryQuote) -> API DTO (QuoteResponse).
 * Обеспечивает изоляцию доменного слоя от веб-слоя.
 */
@Mapper(componentModel = "spring")
public interface QuoteApiMapper {

    QuoteResponse toResponse(DeliveryQuote quote);
}
