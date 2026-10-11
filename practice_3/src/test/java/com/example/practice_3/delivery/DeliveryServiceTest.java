package com.example.practice_3.delivery;

import com.example.practice_3.configuration.DeliveryProperties;
import com.example.practice_3.notify.DeliveryNotifier;
import com.example.practice_3.notify.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-09-21T04:00:00Z"), ZoneOffset.UTC);

    // Мокаем репозитории — тест без БД, только бизнес-логика
    @Mock
    private RegionRepository regionRepository;

    @Mock
    private QuoteRecordRepository quoteRepository;

    @Test
    void notifiesEveryChannelAndMarksFreeDelivery() {
        var sent = new ArrayList<String>();
        NotificationSender fake = new NotificationSender() {
            @Override public String channel() { return "fake"; }
            @Override public void send(String message) { sent.add(message); }
        };

        var notifier    = new DeliveryNotifier(List.of(fake));
        var properties  = new DeliveryProperties(15_000, List.of("ALA", "AST"), Duration.ofHours(48));

        // Говорим моку: findByCode("ALA") → вернуть регион
        Region fakeRegion = Region.builder().id(1L).code("ALA").name("Almaty").build();
        when(regionRepository.findByCode("ALA")).thenReturn(Optional.of(fakeRegion));
        when(quoteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var service = new DeliveryService(properties, notifier, clock, regionRepository, quoteRepository);

        DeliveryQuote quote = service.quote("ALA", 20_000);

        assertThat(quote.freeDelivery()).isTrue();
        assertThat(quote.quotedAt()).isEqualTo(Instant.parse("2026-09-21T04:00:00Z"));
        assertThat(quote.notifiedVia()).containsExactly("fake");
        assertThat(sent).hasSize(1);
    }

    @Test
    void rejectsUnknownRegion() {
        var notifier   = new DeliveryNotifier(List.of());
        var properties = new DeliveryProperties(15_000, List.of("ALA"), Duration.ofHours(48));
        var service    = new DeliveryService(properties, notifier, clock, regionRepository, quoteRepository);

        assertThatThrownBy(() -> service.quote("SHY", 1))
                .isInstanceOf(UnservedRegionException.class)
                .hasMessageContaining("SHY");
    }
}
