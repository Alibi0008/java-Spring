package com.example.practice_3.delivery.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO для элемента истории расчётов (API уровень).
 */
@Schema(description = "Запись из истории расчётов доставки")
public record QuoteRecordDto(
        @Schema(description = "Идентификатор записи в базе данных", example = "1")
        Long id,

        @Schema(description = "Код региона", example = "ALA")
        String regionCode,

        @Schema(description = "Наименование региона", example = "Almaty")
        String regionName,

        @Schema(description = "Сумма заказа", example = "20000")
        long orderAmount,

        @Schema(description = "Бесплатная доставка", example = "true")
        boolean freeDelivery,

        @Schema(description = "Момент расчёта (UTC)")
        Instant quotedAt,

        @Schema(description = "Срок доставки в часах", example = "48")
        int slaHours
) {
}
