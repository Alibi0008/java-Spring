package com.example.practice_3.delivery.infra.tariff;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Resilient Gateway with Circuit Breaker, Retry, and Fallback (Week 10, slides 25, 26).
 */
@Service
public class TariffGateway {

    private static final Logger log = LoggerFactory.getLogger(TariffGateway.class);
    private final TariffClient tariffClient;

    public TariffGateway(TariffClient tariffClient) {
        this.tariffClient = tariffClient;
    }

    /**
     * Запрашивает тарифный коэффициент региона.
     * В случае сбоя или таймаута вызывается fallback-метод.
     */
    @Retry(name = "tariff")
    @CircuitBreaker(name = "tariff", fallbackMethod = "fallbackMultiplier")
    public double getMultiplier(String region) {
        TariffResponse response = tariffClient.getTariffMultiplier(region);
        return response != null ? response.multiplier() : 1.0;
    }

    /**
     * Fallback при недоступности удалённого сервиса тарифов (слайд 26).
     * Возвращает базовый коэффициент 1.0 без наценок, система продолжает работать.
     */
    public double fallbackMultiplier(String region, Throwable cause) {
        log.warn("Remote tariff service failed for region {}: {}. Using default fallback multiplier 1.0",
                region, cause.getMessage());
        return 1.0;
    }
}
