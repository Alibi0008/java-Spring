package com.example.practice_3.delivery;

import java.time.Instant;
import java.time.Duration;
import java.util.List;

public record DeliveryQuote(
        String region,
        long orderAmount,
        boolean freeDelivery,
        Duration slaWindow,
        Instant quotedAt,
        List<String> notifiedVia
) {
}
