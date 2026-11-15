package com.example.practice_3.delivery.infra.tariff;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit test for TariffGateway verifying fallback behavior on remote service failure (Week 10, slides 25, 26, 30).
 */
@ExtendWith(MockitoExtension.class)
class TariffGatewayTest {

    @Mock
    private TariffClient tariffClient;

    @InjectMocks
    private TariffGateway tariffGateway;

    @Test
    void returnsMultiplierWhenServiceIsHealthy() {
        when(tariffClient.getTariffMultiplier("ALA"))
                .thenReturn(new TariffResponse("ALA", 1.5));

        double multiplier = tariffGateway.getMultiplier("ALA");

        assertThat(multiplier).isEqualTo(1.5);
    }

    @Test
    void fallbackMultiplierReturnsDefaultOneOnFailure() {
        double result = tariffGateway.fallbackMultiplier("ALA", new ResourceAccessException("Connection timed out"));

        assertThat(result).isEqualTo(1.0);
    }
}
