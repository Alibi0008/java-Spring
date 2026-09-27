package com.example.practice_3.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("telegram")
@ConditionalOnProperty(prefix = "store.notify.telegram", name = "enabled", havingValue = "true")
public class TelegramNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationSender.class);

    @Override
    public String channel() {
        return "telegram";
    }

    @Override
    public void send(String message) {
        log.info("[telegram] {}", message);
    }
}
