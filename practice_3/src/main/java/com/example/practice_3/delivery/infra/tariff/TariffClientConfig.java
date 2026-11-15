package com.example.practice_3.delivery.infra.tariff;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration for TariffClient with explicit connect and read timeouts (Week 10, slides 19, 21, 22).
 */
@Configuration
@EnableConfigurationProperties(TariffClientProperties.class)
public class TariffClientConfig {

    @Bean
    public RestClient tariffRestClient(TariffClientProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) properties.connectTimeout().toMillis());
        factory.setReadTimeout((int) properties.readTimeout().toMillis());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .defaultHeader("X-Client", "delivery-service")
                .build();
    }

    @Bean
    public TariffClient tariffClient(RestClient tariffRestClient) {
        RestClientAdapter adapter = RestClientAdapter.create(tariffRestClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(TariffClient.class);
    }
}
