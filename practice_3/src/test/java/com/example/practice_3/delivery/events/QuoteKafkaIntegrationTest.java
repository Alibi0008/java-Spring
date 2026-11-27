package com.example.practice_3.delivery.events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Интеграционный тест Kafka (SIS 3):
 * 1) Публикация и обработка QuoteCalculatedEvent с ключом = quoteId
 * 2) Идемпотентность консьюмера (дедупликация через таблицу processed_events)
 * 3) Отправка "ядовитого" сообщения (poison message) в Dead-Letter Topic (quotes.calculated.DLT)
 */
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.listener.auto-startup=true"
})
@EmbeddedKafka(
        partitions = 3,
        topics = {QuoteEventsProducer.TOPIC, QuoteEventsProducer.DLT_TOPIC}
)
@DirtiesContext
class QuoteKafkaIntegrationTest {

    @Autowired
    private QuoteEventsProducer producer;

    @Autowired
    private QuoteAnalyticsConsumer consumer;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Test
    void processesEventOnceAndRoutesPoisonMessageToDlt() {
        int initialProcessed = consumer.getProcessedCount();
        int initialDlt = consumer.getDltCount();

        // 1. Отправляем валидное событие
        UUID eventId = UUID.randomUUID();
        QuoteCalculatedEvent validEvent = new QuoteCalculatedEvent(
                eventId, 42L, "ALA", 20000L, true, Instant.now()
        );
        producer.publish(validEvent).join();

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(consumer.getProcessedCount()).isEqualTo(initialProcessed + 1);
            assertThat(processedEventRepository.existsById(eventId)).isTrue();
        });

        // 2. Отправляем дубликат с тем же eventId — консьюмер должен проигнорировать его (идемпотентность)
        producer.publish(validEvent).join();

        await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(consumer.getProcessedCount()).isEqualTo(initialProcessed + 1)
        );

        // 3. Отправляем "ядовитое" сообщение (region = POISON) — должно попасть в DLT
        UUID poisonId = UUID.randomUUID();
        QuoteCalculatedEvent poisonEvent = new QuoteCalculatedEvent(
                poisonId, 999L, "POISON", 1000L, false, Instant.now()
        );
        producer.publish(poisonEvent).join();

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(consumer.getDltCount()).isEqualTo(initialDlt + 1);
            assertThat(processedEventRepository.existsById(poisonId)).isFalse();
        });
    }
}
