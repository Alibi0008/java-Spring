# Delivery Quote Service (`practice_3`)

Spring Boot 4 / Java 21 сервис расчёта стоимости и сроков доставки с:
- **OpenAPI / Swagger UI** (`http://localhost:8080/swagger-ui/index.html`)
- **Resilient `RestClient`** (`TariffClient` + `TariffGateway` с таймаутами, Retry, CircuitBreaker и Fallback на `1.0`)
- **AOP + Bounded Cache** (`ExecutionTimeAspect` + Caffeine `maximumSize=500, expireAfterWrite=10m`)
- **Spring Security + JWT** (`/api/auth/login`, BCrypt, роли `STUDENT` и `ADMIN`)
- **Event-Driven Architecture & Apache Kafka (SIS 3)**:
  - Доменное событие `QuoteCalculatedEvent`, публикуемое после коммита (`AFTER_COMMIT`) в топик `quotes.calculated` с ключом `quoteId`
  - Идемпотентный консьюмер `QuoteAnalyticsConsumer` (группа `quote-analytics-service`, дедупликация через таблицу `processed_events`)
  - Dead-Letter Topic (`quotes.calculated.DLT`) через `DefaultErrorHandler` + `DeadLetterPublishingRecoverer`

---

## Как запустить Kafka локально (SIS 3)

### Вариант 1: Через Docker Compose (рекомендуемый)
В корне проекта есть `compose.yaml` с `apache/kafka:3.8.0` (KRaft, без ZooKeeper).

```bash
docker compose up -d
```

Проверить, что брокер запущен:
```bash
docker compose ps
```

Остановить Kafka:
```bash
docker compose down
```

### Вариант 2: Без Docker (через встроенные тесты)
Проект содержит интеграционный тест `QuoteKafkaIntegrationTest` на базе `@EmbeddedKafka`, который поднимает Kafka в памяти автоматически при запуске тестов.

---

## Запуск приложения и тестов

### Запуск тестов и сборки (с JaCoCo отчётом)
```bash
./mvnw verify
```

### Запуск приложения
```bash
./mvnw spring-boot:run
```

После запуска:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Пользователи (из Flyway `V3`):
  - `student` / `student123` (`ROLE_STUDENT`)
  - `admin` / `admin123` (`ROLE_ADMIN`)

---

## Демонстрация Poison Message и Dead-Letter Topic (DLT)

1. При отправке события `QuoteCalculatedEvent` с `region = "POISON"` (или `orderAmount <= 0`) консьюмер `QuoteAnalyticsConsumer` выбрасывает `InvalidQuoteEventException`.
2. `KafkaErrorConfig` (`DefaultErrorHandler` + `DeadLetterPublishingRecoverer`) перехватывает ошибку и направляет сообщение в топик **`quotes.calculated.DLT`**.
3. `QuoteAnalyticsConsumer#onDltEvent` (группа `quote-dlt-monitor`) логирует попадание сообщения в DLT.
4. Этот сценарий и идемпотентность консьюмера автоматически проверяются в `QuoteKafkaIntegrationTest`.
