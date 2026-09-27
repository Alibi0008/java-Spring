# Practice 3 — IoC container and dependency injection

Refactor of the service layer to **constructor injection** plus one **conditional bean**, continuing the delivery settings from Practice 2.

## What was done

- Service-layer classes (`DeliveryService`, `DeliveryNotifier`) take dependencies through a constructor; fields are `private final`.
- No `@Autowired` on fields in the service layer (or anywhere in production code).
- One conditional bean: `TelegramNotificationSender`, registered only when `store.notify.telegram.enabled=true`.
- The app starts with the condition **on** (profile `dev`) and **off** (default / `test`).
- `DeliveryNotifier` injects `List<NotificationSender>`, so Telegram can disappear without breaking start-up.

## How to toggle the conditional bean

Default (`application.yml`): Telegram is **off**.

```bash
./mvnw spring-boot:run
```

Log line: `Active notification channels: [email]`

Turn Telegram **on** with the Practice 2 `dev` profile (`application-dev.yml` sets `store.notify.telegram.enabled: true`):

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

Log line: `Active notification channels: [email, telegram]`

Same switch without a profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--store.notify.telegram.enabled=true
```

Try a quote:

```text
GET http://localhost:8080/api/delivery/quote?region=ALA&amount=20000
```

## Tests

```bash
./mvnw test
```

- `DeliveryServiceTest` — unit test with `new DeliveryService(...)`, no Spring.
- `Practice3ApplicationTests` — profile `test`, Telegram bean absent.
- `TelegramEnabledContextTest` — profile `dev`, Telegram bean present.
