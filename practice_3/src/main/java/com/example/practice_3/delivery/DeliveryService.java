package com.example.practice_3.delivery;

import com.example.practice_3.configuration.DeliveryProperties;
import com.example.practice_3.notify.DeliveryNotifier;
import com.example.practice_3.web.dto.QuoteRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional           // все публичные методы — в транзакции по умолчанию
public class DeliveryService {

    private final DeliveryProperties properties;
    private final DeliveryNotifier notifier;
    private final Clock clock;
    private final RegionRepository regionRepository;
    private final QuoteRecordRepository quoteRepository;

    public DeliveryService(DeliveryProperties properties,
                           DeliveryNotifier notifier,
                           Clock clock,
                           RegionRepository regionRepository,
                           QuoteRecordRepository quoteRepository) {
        this.properties = properties;
        this.notifier = notifier;
        this.clock = clock;
        this.regionRepository = regionRepository;
        this.quoteRepository = quoteRepository;
    }

    /**
     * Рассчитывает стоимость доставки и сохраняет запись в БД.
     * Транзакция: открывается при входе, коммитится при выходе.
     */
    public DeliveryQuote quote(String region, long orderAmount) {
        if (!properties.regions().contains(region)) {
            throw new UnservedRegionException("Region is not served: " + region);
        }

        boolean freeDelivery = orderAmount >= properties.freeFrom();
        Instant quotedAt = Instant.now(clock);
        List<String> notifiedVia = notifier.notifyAll(
                "Quote for %s amount=%d free=%s at %s".formatted(region, orderAmount, freeDelivery, quotedAt)
        );

        // Находим регион в БД (data.sql гарантирует что он есть)
        Region regionEntity = regionRepository.findByCode(region)
                .orElseThrow(() -> new UnservedRegionException("Region not found in DB: " + region));

        // Сохраняем историю расчёта
        quoteRepository.save(QuoteRecord.builder()
                .region(regionEntity)
                .orderAmount(orderAmount)
                .freeDelivery(freeDelivery)
                .quotedAt(quotedAt)
                .slaHours((int) properties.slaWindow().toHours())
                .build());

        return new DeliveryQuote(region, orderAmount, freeDelivery,
                properties.slaWindow(), quotedAt, notifiedVia);
    }

    /**
     * Возвращает историю расчётов постранично.
     * readOnly=true — Hibernate не делает dirty check, быстрее.
     * Маппинг в DTO выполняется внутри транзакции.
     */
    @Transactional(readOnly = true)
    public Page<QuoteRecordDto> listQuotes(String regionCode, Pageable pageable) {
        Page<QuoteRecord> page;
        if (regionCode != null && !regionCode.isBlank()) {
            page = quoteRepository.findByRegionCode(regionCode, pageable);
        } else {
            page = quoteRepository.findAll(pageable);
        }
        return page.map(QuoteRecordDto::from);
    }
}
