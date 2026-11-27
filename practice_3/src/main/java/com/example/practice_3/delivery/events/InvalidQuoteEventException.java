package com.example.practice_3.delivery.events;

/**
 * Исключение для некорректных ("ядовитых") событий из Kafka,
 * которые не подлежат повторной обработке и должны сразу отправляться в DLT.
 */
public class InvalidQuoteEventException extends RuntimeException {
    public InvalidQuoteEventException(String message) {
        super(message);
    }
}
