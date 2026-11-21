package com.example.practice_3.security.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        @Schema(example = "admin", description = "Имя пользователя")
        String username,

        @NotBlank
        @Schema(example = "admin123", description = "Пароль")
        String password
) {
}
