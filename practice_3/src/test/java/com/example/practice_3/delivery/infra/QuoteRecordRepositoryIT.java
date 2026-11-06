package com.example.practice_3.delivery.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration test against real PostgreSQL via Testcontainers.
 * (Week 8, slide 14, 30).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class QuoteRecordRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private QuoteRecordRepository quoteRepository;

    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void setUp() {
        Region almaty = regionRepository.findByCode("ALA").orElseGet(() ->
                regionRepository.save(Region.builder().code("ALA").name("Almaty").build())
        );

        quoteRepository.save(QuoteRecord.builder()
                .region(almaty)
                .orderAmount(25000)
                .freeDelivery(true)
                .quotedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .slaHours(48)
                .build());

        quoteRepository.save(QuoteRecord.builder()
                .region(almaty)
                .orderAmount(5000)
                .freeDelivery(false)
                .quotedAt(Instant.parse("2026-10-18T11:00:00Z"))
                .slaHours(48)
                .build());
    }

    @Test
    void findsQuotesWithEntityGraph() {
        Page<QuoteRecord> page = quoteRepository.findByRegionCode("ALA", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getRegion().getName()).isEqualTo("Almaty");
    }

    @Test
    void findsFreeDeliveryQuotes() {
        List<QuoteRecord> free = quoteRepository.findFreeDeliveryByRegionCode("ALA");
        assertThat(free).hasSize(1);
        assertThat(free.get(0).isFreeDelivery()).isTrue();
    }
}
