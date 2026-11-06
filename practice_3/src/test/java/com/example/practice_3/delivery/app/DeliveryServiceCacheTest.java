package com.example.practice_3.delivery.app;

import com.example.practice_3.configuration.DeliveryProperties;
import com.example.practice_3.delivery.domain.DeliveryPolicy;
import com.example.practice_3.delivery.domain.DeliveryQuote;
import com.example.practice_3.delivery.infra.QuoteEntityMapper;
import com.example.practice_3.delivery.infra.QuoteRecord;
import com.example.practice_3.delivery.infra.QuoteRecordRepository;
import com.example.practice_3.delivery.infra.Region;
import com.example.practice_3.delivery.infra.RegionRepository;
import com.example.practice_3.delivery.web.dto.QuoteRecordDto;
import com.example.practice_3.notify.DeliveryNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for caching and cache eviction on DeliveryService (Practice 5).
 */
@SpringBootTest
class DeliveryServiceCacheTest {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private QuoteRecordRepository quoteRepository;

    @MockitoBean
    private RegionRepository regionRepository;

    @BeforeEach
    void clearCache() {
        var cache = cacheManager.getCache("quotes");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void cachesRepeatedCallsToListQuotes() {
        Pageable pageable = PageRequest.of(0, 10);
        QuoteRecord fakeRecord = QuoteRecord.builder()
                .id(1L)
                .region(Region.builder().code("ALA").name("Almaty").build())
                .orderAmount(20000)
                .freeDelivery(true)
                .quotedAt(Instant.now())
                .slaHours(48)
                .build();

        when(quoteRepository.findByRegionCode(eq("ALA"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(fakeRecord)));

        // Первый вызов — попадает в репозиторий (Cache Miss)
        var firstResult = deliveryService.listQuotes("ALA", pageable);
        assertThat(firstResult.getContent()).hasSize(1);
        verify(quoteRepository, times(1)).findByRegionCode(eq("ALA"), eq(pageable));

        // Второй вызов с теми же параметрами — возвращается из кэша (Cache Hit)
        var secondResult = deliveryService.listQuotes("ALA", pageable);
        assertThat(secondResult.getContent()).hasSize(1);
        // Репозиторий больше НЕ вызывался
        verify(quoteRepository, times(1)).findByRegionCode(eq("ALA"), eq(pageable));
    }

    @Test
    void evictsCacheWhenNewQuoteIsCreated() {
        Pageable pageable = PageRequest.of(0, 10);
        QuoteRecord fakeRecord = QuoteRecord.builder()
                .id(1L)
                .region(Region.builder().code("ALA").name("Almaty").build())
                .orderAmount(20000)
                .freeDelivery(true)
                .quotedAt(Instant.now())
                .slaHours(48)
                .build();

        when(quoteRepository.findByRegionCode(eq("ALA"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(fakeRecord)));

        // 1. Читаем и кэшируем
        deliveryService.listQuotes("ALA", pageable);
        verify(quoteRepository, times(1)).findByRegionCode(eq("ALA"), eq(pageable));

        // 2. Создаем новый расчёт (должен сработать @CacheEvict)
        Region ala = Region.builder().code("ALA").name("Almaty").build();
        when(regionRepository.findByCode("ALA")).thenReturn(Optional.of(ala));
        when(quoteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        deliveryService.quote("ALA", 18000);

        // 3. Снова вызываем чтение — кэш был сброшен, поэтому снова обращается к репозиторию
        deliveryService.listQuotes("ALA", pageable);
        verify(quoteRepository, times(2)).findByRegionCode(eq("ALA"), eq(pageable));
    }
}
