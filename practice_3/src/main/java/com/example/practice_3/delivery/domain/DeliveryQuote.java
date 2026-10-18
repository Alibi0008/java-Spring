package com.example.practice_3.delivery.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Доменный объект — результат расчёта доставки.
 * Чистая Java, без зависимостей от Spring и JPA.
 */
public record DeliveryQuote(
        String region,
        long orderAmount,
        boolean freeDelivery,
        Duration slaWindow,
        Instant quotedAt,
        List<String> notifiedVia
) {
    public DeliveryQuote {
        if (orderAmount < 0) {
            throw new IllegalArgumentException("Order amount cannot be negative: " + orderAmount);
        }
    }
}
