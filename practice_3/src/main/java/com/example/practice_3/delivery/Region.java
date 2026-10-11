package com.example.practice_3.delivery;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Регион доставки. Хранится в таблице region.
 * Связь 1:* с QuoteRecord — один регион может иметь множество расчётов.
 */
@Entity
@Table(name = "region")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;   // ALA, AST, SHY

    @Column(nullable = false, length = 100)
    private String name;   // Almaty, Astana, Shymkent

    // LAZY — не грузим список при каждом запросе региона
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuoteRecord> quotes = new ArrayList<>();
}
