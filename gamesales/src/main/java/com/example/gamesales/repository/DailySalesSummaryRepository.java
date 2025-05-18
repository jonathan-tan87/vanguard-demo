package com.example.gamesales.repository;

import com.example.gamesales.entity.DailySalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailySalesSummaryRepository extends JpaRepository<DailySalesSummary, LocalDate> {
    List<DailySalesSummary> findBySummaryDateBetweenOrderBySummaryDateAsc(LocalDate fromDate, LocalDate toDate);

    @Modifying
    @Query(value = "INSERT INTO daily_sales_summary (summary_date, total_games_sold, total_sales_amount) " +
                   "VALUES (:summaryDate, :gamesSold, :salesAmount) ", nativeQuery = true)
    void insertSummary(@Param("summaryDate") LocalDate summaryDate,
                       @Param("gamesSold") Long gamesSold,
                       @Param("salesAmount") BigDecimal salesAmount);
}