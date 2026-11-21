package com.example.practice_3.security;

import com.example.practice_3.delivery.app.DeliveryService;
import com.example.practice_3.delivery.web.DeliveryQuoteController;
import com.example.practice_3.delivery.web.QuoteApiMapper;
import com.example.practice_3.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryQuoteController.class)
@Import(SecurityConfig.class)
class DeliverySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

    @MockitoBean
    private QuoteApiMapper quoteApiMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("GET /api/v1/quotes без токена возвращает 200 OK")
    void anonymousGetQuotes_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/quotes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/quotes без токена возвращает 401 Unauthorized")
    void anonymousCreateQuote_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/quotes")
                        .contentType("application/json")
                        .content("{\"region\":\"ALA\",\"amount\":15000}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/quotes/{id} без токена возвращает 401 Unauthorized")
    void anonymousDeleteQuote_returns401() throws Exception {
        mockMvc.perform(delete("/api/v1/quotes/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/quotes/{id} с ролью STUDENT возвращает 403 Forbidden")
    void studentDeleteQuote_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/quotes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_STUDENT"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/v1/quotes/{id} с ролью ADMIN возвращает 204 No Content")
    void adminDeleteQuote_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/quotes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }
}
