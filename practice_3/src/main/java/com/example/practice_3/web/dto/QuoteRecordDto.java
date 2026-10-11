package com.example.practice_3.web.dto;

import com.example.practice_3.delivery.QuoteRecord;

import java.time.Instant;

/**
 * DTO для элемента истории расчётов.
 * Не возвращаем Entity напрямую — скрываем детали БД и избегаем LAZY-ошибок.
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
    public static QuoteRecordDto from(QuoteRecord q) {
        return new QuoteRecordDto(
                q.getId(),
                q.getRegion().getCode(),
                q.getRegion().getName(),
                q.getOrderAmount(),
                q.isFreeDelivery(),
                q.getQuotedAt(),
                q.getSlaHours()
        );
    }
}
