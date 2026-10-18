package com.example.practice_3.delivery.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    // Derived query — Spring сам генерирует: SELECT * FROM region WHERE code = ?
    Optional<Region> findByCode(String code);

    // @Query — JPQL с JOIN FETCH чтобы не было N+1 проблемы
    @Query("select r from Region r left join fetch r.quotes where size(r.quotes) > 0")
    List<Region> findRegionsWithQuotes();
}
