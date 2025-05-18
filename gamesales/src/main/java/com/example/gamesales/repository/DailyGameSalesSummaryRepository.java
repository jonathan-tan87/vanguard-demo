package com.example.gamesales.repository;

import com.example.gamesales.entity.DailyGameSalesSummary;
import com.example.gamesales.entity.DailyGameSalesSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyGameSalesSummaryRepository extends JpaRepository<DailyGameSalesSummary, DailyGameSalesSummaryId> {
    List<DailyGameSalesSummary> findBySummaryDateBetweenOrderBySummaryDateAscGameNoAsc(LocalDate fromDate, LocalDate toDate);
    List<DailyGameSalesSummary> findBySummaryDateBetweenAndGameNoOrderBySummaryDateAsc(LocalDate fromDate, LocalDate toDate, Integer gameNo);

    @Modifying
    @Query(value = "INSERT INTO daily_game_sales_summary (summary_date, game_no, total_games_sold, total_sales_amount) " +
                   "VALUES (:summaryDate, :gameNo, :gamesSold, :salesAmount) ", nativeQuery = true)
    void insertSummary(@Param("summaryDate") LocalDate summaryDate,
                       @Param("gameNo") Integer gameNo,
                       @Param("gamesSold") Long gamesSold,
                       @Param("salesAmount") BigDecimal salesAmount);
}