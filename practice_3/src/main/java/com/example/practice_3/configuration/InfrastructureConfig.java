package com.example.practice_3.configuration;

import com.example.practice_3.notify.DeliveryNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class InfrastructureConfig {

    private static final Logger log = LoggerFactory.getLogger(InfrastructureConfig.class);

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Asia/Almaty"));
    }

    @Bean
    CommandLineRunner logNotificationChannels(DeliveryNotifier notifier) {
        return args -> log.info("Active notification channels: {}", notifier.channels());
    }
}
