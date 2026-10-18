package com.example.practice_3.delivery.app;

import com.example.practice_3.configuration.DeliveryProperties;
import com.example.practice_3.delivery.domain.DeliveryPolicy;
import com.example.practice_3.delivery.domain.DeliveryQuote;
import com.example.practice_3.delivery.domain.UnservedRegionException;
import com.example.practice_3.delivery.infra.QuoteEntityMapper;
import com.example.practice_3.delivery.infra.QuoteRecord;
import com.example.practice_3.delivery.infra.QuoteRecordRepository;
import com.example.practice_3.delivery.infra.Region;
import com.example.practice_3.delivery.infra.RegionRepository;
import com.example.practice_3.delivery.web.dto.QuoteRecordDto;
import com.example.practice_3.notify.DeliveryNotifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Application Service: оркестрирует выполнение бизнес-сценариев.
 * Принимает запросы, применяет доменную политику, сохраняет данные и возвращает результат.
 */
@Service
@Transactional
public class DeliveryService {

    private final DeliveryProperties properties;
    private final DeliveryNotifier notifier;
    private final Clock clock;
    private final RegionRepository regionRepository;
    private final QuoteRecordRepository quoteRepository;
    private final QuoteEntityMapper quoteEntityMapper;
    private final DeliveryPolicy deliveryPolicy;

    public DeliveryService(DeliveryProperties properties,
                           DeliveryNotifier notifier,
                           Clock clock,
                           RegionRepository regionRepository,
                           QuoteRecordRepository quoteRepository,
                           QuoteEntityMapper quoteEntityMapper,
                           DeliveryPolicy deliveryPolicy) {
        this.properties = properties;
        this.notifier = notifier;
        this.clock = clock;
        this.regionRepository = regionRepository;
        this.quoteRepository = quoteRepository;
        this.quoteEntityMapper = quoteEntityMapper;
        this.deliveryPolicy = deliveryPolicy;
    }

    /**
     * Рассчитывает стоимость доставки и сохраняет запись в БД.
     */
    public DeliveryQuote quote(String region, long orderAmount) {
        deliveryPolicy.validateOrderAmount(orderAmount);

        if (!properties.regions().contains(region)) {
            throw new UnservedRegionException("Region is not served: " + region);
        }

        boolean freeDelivery = deliveryPolicy.isEligibleForFreeDelivery(orderAmount, properties.freeFrom());
        Instant quotedAt = Instant.now(clock);
        List<String> notifiedVia = notifier.notifyAll(
                "Quote for %s amount=%d free=%s at %s".formatted(region, orderAmount, freeDelivery, quotedAt)
        );

        Region regionEntity = regionRepository.findByCode(region)
                .orElseThrow(() -> new UnservedRegionException("Region not found in DB: " + region));

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
     * readOnly=true — без dirty check.
     * Маппинг в DTO выполняется через MapStruct внутри транзакции.
     */
    @Transactional(readOnly = true)
    public Page<QuoteRecordDto> listQuotes(String regionCode, Pageable pageable) {
        Page<QuoteRecord> page;
        if (regionCode != null && !regionCode.isBlank()) {
            page = quoteRepository.findByRegionCode(regionCode, pageable);
        } else {
            page = quoteRepository.findAll(pageable);
        }
        return page.map(quoteEntityMapper::toDto);
    }
}
