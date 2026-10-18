package com.example.practice_3.delivery.web;

import com.example.practice_3.delivery.app.DeliveryService;
import com.example.practice_3.delivery.domain.DeliveryQuote;
import com.example.practice_3.delivery.domain.UnservedRegionException;
import com.example.practice_3.delivery.web.dto.QuoteRecordDto;
import com.example.practice_3.web.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web Slice test (@WebMvcTest).
 * Тестирует только веб-слой (контроллер, валидация, HTTP статус-коды, JSON)
 * без поднятия базы данных и полного Spring контекста.
 */
@WebMvcTest(DeliveryQuoteController.class)
@Import({QuoteApiMapperImpl.class, ApiExceptionHandler.class})
class DeliveryQuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

    @Test
    void quoteEndpointReturnsOkOnValidRequest() throws Exception {
        DeliveryQuote fakeQuote = new DeliveryQuote(
                "ALA",
                25000,
                true,
                Duration.ofHours(48),
                Instant.parse("2026-10-18T10:00:00Z"),
                List.of("email")
        );
        when(deliveryService.quote("ALA", 25000)).thenReturn(fakeQuote);

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "region": "ALA",
                                    "amount": 25000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("ALA"))
                .andExpect(jsonPath("$.orderAmount").value(25000))
                .andExpect(jsonPath("$.freeDelivery").value(true))
                .andExpect(jsonPath("$.notifiedVia[0]").value("email"));
    }

    @Test
    void quoteEndpointReturnsBadRequestOnInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "region": "ALA",
                                    "amount": -500
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quoteEndpointReturnsNotFoundOnUnservedRegion() throws Exception {
        when(deliveryService.quote(eq("UNKNOWN"), any(Long.class)))
                .thenThrow(new UnservedRegionException("Region is not served: UNKNOWN"));

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "region": "UNKNOWN",
                                    "amount": 10000
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void listQuotesReturnsPagedResponse() throws Exception {
        QuoteRecordDto item = new QuoteRecordDto(
                1L,
                "ALA",
                "Almaty",
                20000,
                true,
                Instant.parse("2026-10-18T10:00:00Z"),
                48
        );
        when(deliveryService.listQuotes(eq("ALA"), any())).thenReturn(
                new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1)
        );

        mockMvc.perform(get("/api/v1/quotes")
                        .param("region", "ALA")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].regionCode").value("ALA"))
                .andExpect(jsonPath("$.content[0].regionName").value("Almaty"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
