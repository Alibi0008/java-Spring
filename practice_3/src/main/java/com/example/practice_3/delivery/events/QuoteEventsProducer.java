package com.example.practice_3.delivery.events;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Продюсер доменных событий расчёта доставки в Kafka.
 * Ключ сообщения = quoteId (aggregate id), что гарантирует порядок событий по конкретной котировке.
 */
@Component
public class QuoteEventsProducer {

    public static final String TOPIC = "quotes.calculated";
    public static final String DLT_TOPIC = "quotes.calculated.DLT";

    private static final Logger log = LoggerFactory.getLogger(QuoteEventsProducer.class);

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public QuoteEventsProducer(KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<Object, Object>> publish(QuoteCalculatedEvent event) {
        String key = String.valueOf(event.quoteId());
        return kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish QuoteCalculatedEvent eventId={} quoteId={}",
                                event.eventId(), event.quoteId(), ex);
                    } else {
                        var meta = result.getRecordMetadata();
                        log.info("Published QuoteCalculatedEvent eventId={} key={} to {}-{}@{}",
                                event.eventId(), key, meta.topic(), meta.partition(), meta.offset());
                    }
                });
    }

    @Configuration
    static class TopicConfig {

        @Bean
        NewTopic quotesCalculatedTopic() {
            return TopicBuilder.name(TOPIC)
                    .partitions(3)
                    .replicas(1)
                    .build();
        }

        @Bean
        NewTopic quotesCalculatedDltTopic() {
            return TopicBuilder.name(DLT_TOPIC)
                    .partitions(1)
                    .replicas(1)
                    .build();
        }
    }
}
