package com.example.practice_3.delivery.infra.tariff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Client test with MockRestServiceServer (Week 10, slide 27).
 */
class TariffClientTest {

    private MockRestServiceServer server;
    private TariffClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8089");
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        client = factory.createClient(TariffClient.class);
    }

    @Test
    void getsTariffMultiplierSuccessfully() {
        server.expect(requestTo("http://localhost:8089/tariffs/ALA"))
                .andRespond(withSuccess("""
                        {
                            "region": "ALA",
                            "multiplier": 1.25
                        }
                        """, MediaType.APPLICATION_JSON));

        TariffResponse response = client.getTariffMultiplier("ALA");

        assertThat(response).isNotNull();
        assertThat(response.region()).isEqualTo("ALA");
        assertThat(response.multiplier()).isEqualTo(1.25);
    }
}
