package com.example.practice_3.delivery.events;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Конфигурация обработки ошибок Kafka:
 * повторы с фиксированной задержкой и отправка необработанных ("ядовитых") сообщений в <topic>.DLT.
 */
@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template) {
        var recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", 0));
        var handler = new DefaultErrorHandler(recoverer, new FixedBackOff(500L, 2));
        handler.addNotRetryableExceptions(InvalidQuoteEventException.class);
        return handler;
    }
}
