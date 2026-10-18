package com.example.practice_3.delivery.domain;

import org.springframework.stereotype.Component;

/**
 * Доменный сервис (Domain Service) для бизнес-правил расчёта доставки.
 * Инкапсулирует бизнес-логику определения бесплатной доставки и валидации заказа.
 */
@Component
public class DeliveryPolicy {

    /**
     * Проверяет, полагается ли бесплатная доставка по сумме заказа.
     */
    public boolean isEligibleForFreeDelivery(long orderAmount, long freeDeliveryThreshold) {
        return orderAmount >= freeDeliveryThreshold;
    }

    /**
     * Валидирует сумму заказа согласно бизнес-правилам.
     */
    public void validateOrderAmount(long orderAmount) {
        if (orderAmount <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }
    }
}
