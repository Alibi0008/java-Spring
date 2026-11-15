package com.example.practice_3.delivery.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Запрос на расчёт стоимости и условий доставки")
public record QuoteRequest(
        @Schema(description = "Код обслуживаемого региона (ALA, AST, SHY)", example = "ALA")
        @NotBlank String region,

        @Schema(description = "Сумма заказа в тенге (строго положительная)", example = "25000")
        @Positive long amount
) {
}
