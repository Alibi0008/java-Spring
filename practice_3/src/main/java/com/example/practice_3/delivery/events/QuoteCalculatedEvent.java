package com.example.practice_3.delivery.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Доменное событие: расчёт стоимости доставки выполнен.
 */
public record QuoteCalculatedEvent(
        UUID eventId,
        Long quoteId,
        String region,
        long orderAmount,
        boolean freeDelivery,
        Instant occurredAt
) {
    public static QuoteCalculatedEvent of(Long quoteId, String region, long orderAmount, boolean freeDelivery, Instant occurredAt) {
        return new QuoteCalculatedEvent(UUID.randomUUID(), quoteId, region, orderAmount, freeDelivery, occurredAt);
    }
}
