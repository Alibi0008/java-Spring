package com.example.practice_3;

import com.example.practice_3.notify.DeliveryNotifier;
import com.example.practice_3.notify.TelegramNotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class TelegramEnabledContextTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DeliveryNotifier notifier;

    @Test
    void telegramBeanIsRegisteredWhenPropertyIsTrue() {
        assertThat(context.containsBean("telegram")).isTrue();
        assertThat(context.getBean("telegram")).isInstanceOf(TelegramNotificationSender.class);
        assertThat(notifier.channels()).contains("email", "telegram");
    }
}
