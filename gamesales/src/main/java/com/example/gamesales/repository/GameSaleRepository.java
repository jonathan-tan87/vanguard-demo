package com.example.gamesales.repository;

import com.example.gamesales.entity.GameSale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // For more complex queries if needed
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Repository
public interface GameSaleRepository extends JpaRepository<GameSale, Long>, JpaSpecificationExecutor<GameSale> {
    Page<GameSale> findByDateOfSaleBetween(Timestamp fromDate, Timestamp toDate, Pageable pageable);
    Page<GameSale> findBySalePriceLessThan(BigDecimal price, Pageable pageable);
    Page<GameSale> findBySalePriceGreaterThan(BigDecimal price, Pageable pageable);
}