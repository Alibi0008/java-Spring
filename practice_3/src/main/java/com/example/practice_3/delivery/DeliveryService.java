package com.example.practice_3.delivery;

import com.example.practice_3.configuration.DeliveryProperties;
import com.example.practice_3.notify.DeliveryNotifier;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryProperties properties;
    private final DeliveryNotifier notifier;
    private final Clock clock;

    public DeliveryService(DeliveryProperties properties, DeliveryNotifier notifier, Clock clock) {
        this.properties = properties;
        this.notifier = notifier;
        this.clock = clock;
    }

    public DeliveryQuote quote(String region, long orderAmount) {
        if (!properties.regions().contains(region)) {
            throw new IllegalArgumentException("Region is not served: " + region);
        }

        boolean freeDelivery = orderAmount >= properties.freeFrom();
        Instant quotedAt = Instant.now(clock);
        List<String> notifiedVia = notifier.notifyAll(
                "Quote for %s amount=%d free=%s at %s".formatted(region, orderAmount, freeDelivery, quotedAt)
        );

        return new DeliveryQuote(
                region,
                orderAmount,
                freeDelivery,
                properties.slaWindow(),
                quotedAt,
                notifiedVia
        );
    }
}
