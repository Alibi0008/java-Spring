package com.example.practice_3.security.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Tag(name = "Authentication", description = "Аутентификация и получение JWT токена")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtEncoder encoder;

    public AuthController(AuthenticationManager authManager, JwtEncoder encoder) {
        this.authManager = authManager;
        this.encoder = encoder;
    }

    @Operation(summary = "Вход в систему", description = "Аутентифицирует пользователя и возвращает JWT токен на 15 минут")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход, возвращен JWT токен"),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    })
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(req.username(), req.password())
        );

        Instant now = Instant.now();
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("delivery-service")
                .subject(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();

        String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenResponse(token, 900);
    }
}
