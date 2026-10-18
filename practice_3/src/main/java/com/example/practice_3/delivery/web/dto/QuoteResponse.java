package com.example.practice_3.delivery.web.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record QuoteResponse(
        String region,
        long orderAmount,
        boolean freeDelivery,
        Duration slaWindow,
        Instant quotedAt,
        List<String> notifiedVia
) {
}
