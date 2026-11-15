package com.example.practice_3.delivery.infra.tariff;

public record TariffResponse(
        String region,
        double multiplier
) {
}
