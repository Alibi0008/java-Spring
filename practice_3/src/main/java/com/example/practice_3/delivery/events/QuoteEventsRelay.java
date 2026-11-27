package com.example.practice_3.delivery.events;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Публикует доменное событие в Kafka только после успешного коммита транзакции БД (AFTER_COMMIT).
 */
@Component
public class QuoteEventsRelay {

    private final QuoteEventsProducer producer;

    public QuoteEventsRelay(QuoteEventsProducer producer) {
        this.producer = producer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onQuoteCalculated(QuoteCalculatedEvent event) {
        producer.publish(event);
    }
}
