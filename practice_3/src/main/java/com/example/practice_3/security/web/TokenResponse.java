package com.example.practice_3.security.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        @Schema(description = "JWT токен доступа")
        String accessToken,

        @Schema(example = "900", description = "Время жизни токена в секундах (15 минут)")
        long expiresInSeconds,

        @Schema(example = "Bearer", description = "Тип токена")
        String tokenType
) {
    public TokenResponse(String accessToken, long expiresInSeconds) {
        this(accessToken, expiresInSeconds, "Bearer");
    }
}
