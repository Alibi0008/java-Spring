package com.example.practice_3.delivery.infra.tariff;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "services.tariff")
public record TariffClientProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout
) {
    public TariffClientProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8089";
        }
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(1);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(2);
        }
    }
}
