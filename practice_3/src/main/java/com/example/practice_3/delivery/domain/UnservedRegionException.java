package com.example.practice_3.delivery.domain;

/**
 * Доменное исключение: регион не обслуживается или не найден.
 */
public class UnservedRegionException extends RuntimeException {
    public UnservedRegionException(String message) {
        super(message);
    }
}
