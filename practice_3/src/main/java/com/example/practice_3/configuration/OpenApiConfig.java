package com.example.practice_3.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger documentation configuration (Week 10, slide 11).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Quote API")
                        .description("API расчёта стоимости и сроков доставки заказов (Practice 3-6 / SIS 2)")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
