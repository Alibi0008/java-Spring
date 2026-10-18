package com.example.practice_3.delivery.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice test (@DataJpaTest).
 * Тестирует уровень персистентности (JPA Entities, запросы, связи, EntityGraph).
 */
@DataJpaTest
class QuoteRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuoteRecordRepository quoteRepository;

    @Autowired
    private RegionRepository regionRepository;

    private Region almaty;

    @BeforeEach
    void setUp() {
        almaty = Region.builder()
                .code("ALA")
                .name("Almaty")
                .build();
        entityManager.persist(almaty);

        QuoteRecord record1 = QuoteRecord.builder()
                .region(almaty)
                .orderAmount(25000)
                .freeDelivery(true)
                .quotedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .slaHours(48)
                .build();
        entityManager.persist(record1);

        QuoteRecord record2 = QuoteRecord.builder()
                .region(almaty)
                .orderAmount(5000)
                .freeDelivery(false)
                .quotedAt(Instant.parse("2026-10-18T11:00:00Z"))
                .slaHours(48)
                .build();
        entityManager.persist(record2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByRegionCodeWithEntityGraphLoadsRegion() {
        Page<QuoteRecord> page = quoteRepository.findByRegionCode("ALA", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        QuoteRecord first = page.getContent().get(0);
        // Проверяем, что регион подгружен через EntityGraph без LazyInitializationException
        assertThat(first.getRegion().getCode()).isEqualTo("ALA");
        assertThat(first.getRegion().getName()).isEqualTo("Almaty");
    }

    @Test
    void findFreeDeliveryByRegionCodeReturnsOnlyFreeDelivery() {
        List<QuoteRecord> freeQuotes = quoteRepository.findFreeDeliveryByRegionCode("ALA");

        assertThat(freeQuotes).hasSize(1);
        assertThat(freeQuotes.get(0).getOrderAmount()).isEqualTo(25000);
        assertThat(freeQuotes.get(0).isFreeDelivery()).isTrue();
    }

    @Test
    void regionRepositoryFindsByCode() {
        var found = regionRepository.findByCode("ALA");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Almaty");
    }
}
