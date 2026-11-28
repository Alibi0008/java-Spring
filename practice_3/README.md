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
- **Containerisation & Docker Compose (Practice 8)**:
  - Multi-stage layered `Dockerfile` (JDK 21 build → JRE 21 extract → hardened non-root JRE 21 runtime)
  - `compose.yaml` поднимает весь стек: `delivery-service` + `postgres:16` (с named volume `pgdata` и healthcheck) + `apache/kafka:3.8.0` (KRaft)

---

## Запуск всего стека через Docker Compose (Practice 8)

1. Скопируйте шаблон переменных окружения (`.env` добавлен в `.gitignore` и не попадает в Git):
   ```bash
   cp .env.example .env
   ```
   *(Отредактируйте `DB_PASSWORD` в `.env`, если нужно).*

2. Соберите образ и запустите весь стек (PostgreSQL + Kafka + Spring Boot):
   ```bash
   docker compose up -d --build
   ```

3. Проверьте статус и здоровье контейнеров:
   ```bash
   docker compose ps
   docker compose logs -f delivery-service
   ```

4. После старта Flyway автоматически применит миграции `V1`–`V4`, и API будет доступен по адресу:
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
   - Пользователи (из `V3`):
     - `student` / `student123` (`ROLE_STUDENT`)
     - `admin` / `admin123` (`ROLE_ADMIN`)

5. Остановка контейнеров (данные в `pgdata` сохраняются):
   ```bash
   docker compose down
   ```
   *(Чтобы полностью удалить и БД volume: `docker compose down -v`)*

---

## Локальный запуск без Docker (тесты и разработка)

### Запуск всех тестов и сборки (с JaCoCo отчётом)
```bash
./mvnw verify
```
Интеграционный тест `QuoteKafkaIntegrationTest` использует `@EmbeddedKafka` и проверяет публикацию, идемпотентность и отправку poison-сообщений (`region = "POISON"`) в `quotes.calculated.DLT` без необходимости запускать внешний брокер.

### Запуск только Kafka локально (если приложение запускается из IDEA)
```bash
docker compose up -d kafka
```
*(Снаружи контейнера Kafka доступна на `localhost:29092`).*
