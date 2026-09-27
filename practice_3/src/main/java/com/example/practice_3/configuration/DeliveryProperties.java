package com.example.practice_3.configuration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "store.delivery")
public record DeliveryProperties(
        @Min(0) long freeFrom,
        @NotEmpty List<String> regions,
        Duration slaWindow
) {
}
