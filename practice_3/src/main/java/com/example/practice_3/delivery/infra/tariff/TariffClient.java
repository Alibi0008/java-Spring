package com.example.practice_3.delivery.infra.tariff;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Declarative HTTP interface for remote Tariff Service (Week 10, slide 21).
 */
@HttpExchange("/tariffs")
public interface TariffClient {

    @GetExchange("/{region}")
    TariffResponse getTariffMultiplier(@PathVariable("region") String region);
}
