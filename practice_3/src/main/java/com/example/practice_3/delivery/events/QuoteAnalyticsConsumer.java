package com.example.practice_3.delivery.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Идемпотентный консьюмер событий расчёта доставки (отдельная consumer group).
 * Проверяет уникальность eventId через таблицу processed_events.
 */
@Component
public class QuoteAnalyticsConsumer {

    private static final Logger log = LoggerFactory.getLogger(QuoteAnalyticsConsumer.class);

    private final ProcessedEventRepository processedEvents;
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicInteger dltCount = new AtomicInteger(0);

    public QuoteAnalyticsConsumer(ProcessedEventRepository processedEvents) {
        this.processedEvents = processedEvents;
    }

    @KafkaListener(topics = QuoteEventsProducer.TOPIC, groupId = "quote-analytics-service", concurrency = "3")
    @Transactional
    public void onQuoteCalculated(
            QuoteCalculatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        if ("POISON".equalsIgnoreCase(event.region()) || event.orderAmount() <= 0) {
            throw new InvalidQuoteEventException(
                    "Poison message detected for quoteId=" + event.quoteId() + ", region=" + event.region()
            );
        }

        if (processedEvents.existsById(event.eventId())) {
            log.info("Duplicate eventId={} skipped (partition={}, offset={})", event.eventId(), partition, offset);
            return;
        }
        processedEvents.saveAndFlush(new ProcessedEvent(event.eventId(), Instant.now()));

        processedCount.incrementAndGet();
        log.info("Analytics processed quoteId={} region={} amount={} free={} from partition={} offset={}",
                event.quoteId(), event.region(), event.orderAmount(), event.freeDelivery(), partition, offset);
    }

    @KafkaListener(topics = QuoteEventsProducer.DLT_TOPIC, groupId = "quote-dlt-monitor")
    public void onDltEvent(
            QuoteCalculatedEvent event,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String exceptionMessage
    ) {
        dltCount.incrementAndGet();
        log.error("DLT received poison event: eventId={} quoteId={} region={} reason={}",
                event.eventId(), event.quoteId(), event.region(), exceptionMessage);
    }

    public int getProcessedCount() {
        return processedCount.get();
    }

    public int getDltCount() {
        return dltCount.get();
    }
}
