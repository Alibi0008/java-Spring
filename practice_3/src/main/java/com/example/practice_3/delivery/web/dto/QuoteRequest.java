package com.example.practice_3.delivery.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record QuoteRequest(
        @NotBlank String region,
        @Positive long amount
) {
}
