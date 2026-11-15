package com.example.practice_3.delivery.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Schema(description = "Результат расчёта условий и сроков доставки")
public record QuoteResponse(
        @Schema(description = "Код региона", example = "ALA")
        String region,

        @Schema(description = "Сумма заказа в тенге", example = "25000")
        long orderAmount,

        @Schema(description = "Признак бесплатной доставки", example = "true")
        boolean freeDelivery,

        @Schema(description = "Регламентный срок доставки (SLA)", example = "PT48H")
        Duration slaWindow,

        @Schema(description = "Момент фиксации расчёта (UTC)")
        Instant quotedAt,

        @Schema(description = "Каналы доставки уведомлений", example = "[\"email\"]")
        List<String> notifiedVia
) {
}
