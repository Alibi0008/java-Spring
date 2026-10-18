package com.example.practice_3.delivery.web.dto;

import java.time.Instant;

/**
 * DTO для элемента истории расчётов (API уровень).
 * Не возвращаем Entity наружу — скрываем детали JPA и избегаем LazyInitializationException.
 */
public record QuoteRecordDto(
        Long id,
        String regionCode,
        String regionName,
        long orderAmount,
        boolean freeDelivery,
        Instant quotedAt,
        int slaHours
) {
}
