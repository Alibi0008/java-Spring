package com.example.practice_3.delivery.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRecordRepository extends JpaRepository<QuoteRecord, Long> {

    // EntityGraph делает JOIN FETCH для связи region в одном SELECT запросе
    @EntityGraph(attributePaths = {"region"})
    Page<QuoteRecord> findByRegionCode(String code, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"region"})
    Page<QuoteRecord> findAll(Pageable pageable);

    // @Query с явным JOIN FETCH
    @Query("select q from QuoteRecord q join fetch q.region where q.freeDelivery = true and q.region.code = :code")
    List<QuoteRecord> findFreeDeliveryByRegionCode(@Param("code") String code);
}
