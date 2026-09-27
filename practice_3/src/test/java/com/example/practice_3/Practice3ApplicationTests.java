package com.example.practice_3;

import com.example.practice_3.notify.EmailNotificationSender;
import com.example.practice_3.notify.TelegramNotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Practice3ApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoadsWithoutTelegramBean() {
        assertThat(context.getBean(EmailNotificationSender.class)).isNotNull();
        assertThat(context.containsBean("telegram")).isFalse();
        assertThat(context.getBeanNamesForType(TelegramNotificationSender.class)).isEmpty();
    }
}
