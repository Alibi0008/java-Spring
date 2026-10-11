package com.example.practice_3.web.dto;

import com.example.practice_3.delivery.DeliveryQuote;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record QuoteResponse(String region,
                            long orderAmount,
                            boolean freeDelivery,
                            Duration slaWindow,
                            Instant quotedAt,
                            List<String> notifiedVia) {
    public static QuoteResponse from(DeliveryQuote quote){
        return new QuoteResponse(quote.region(), quote.orderAmount(), quote.freeDelivery(), quote.slaWindow(), quote.quotedAt(), quote.notifiedVia());
    }
}
