package com.example.practice_3.delivery.infra;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Запись о расчёте доставки. Хранится в таблице quote_record.
 * Связь *:1 с Region — каждый расчёт относится к одному региону.
 */
@Entity
@Table(name = "quote_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LAZY — не подгружаем регион автоматически, только когда явно нужен
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(nullable = false)
    private long orderAmount;       // сумма заказа

    @Column(nullable = false)
    private boolean freeDelivery;   // бесплатная доставка?

    @Column(nullable = false)
    private Instant quotedAt;       // момент расчёта

    @Column(nullable = false)
    private int slaHours;           // срок доставки в часах
}
