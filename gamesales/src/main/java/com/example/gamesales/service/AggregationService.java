package com.example.gamesales.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.gamesales.entity.GameSale;
import com.example.gamesales.repository.DailyGameSalesSummaryRepository;
import com.example.gamesales.repository.DailySalesSummaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationService {

    private final DailySalesSummaryRepository dailySalesSummaryRepository;
    private final DailyGameSalesSummaryRepository dailyGameSalesSummaryRepository;

    public void updateAggregatesAsync(List<GameSale> gameSalesBatch) {
        if (gameSalesBatch == null || gameSalesBatch.isEmpty()) {
            log.warn("updateAggregatesAsync called with empty or null batch. Skipping.");
            return;
        }

        long startTime = System.currentTimeMillis();
        log.info("Starting ASYNC aggregation for a batch of {} sales. Thread: {}", gameSalesBatch.size(), Thread.currentThread().getName());

        try {
            Map<LocalDate, SalesAggregate> dailySummaries = gameSalesBatch.stream()
                .collect(Collectors.groupingBy(
                    gs -> gs.getDateOfSale().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    Collectors.reducing(
                        new SalesAggregate(0L, BigDecimal.ZERO),
                        gs -> new SalesAggregate(1L, gs.getSalePrice()),
                        (sa1, sa2) -> new SalesAggregate(sa1.count + sa2.count, sa1.totalAmount.add(sa2.totalAmount))
                    )
                ));

            dailySummaries.forEach((date, aggregate) -> {
                log.trace("ASYNC inserting DailySalesSummary for {}: {} sales, {} amount", date, aggregate.count, aggregate.totalAmount);
                dailySalesSummaryRepository.insertSummary(date, aggregate.count, aggregate.totalAmount);
            });

            Map<GameDateKey, SalesAggregate> dailyGameSummaries = gameSalesBatch.stream()
                .collect(Collectors.groupingBy(
                    gs -> new GameDateKey(
                        gs.getDateOfSale().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        gs.getGameNo()
                    ),
                    Collectors.reducing(
                        new SalesAggregate(0L, BigDecimal.ZERO),
                        gs -> new SalesAggregate(1L, gs.getSalePrice()),
                        (sa1, sa2) -> new SalesAggregate(sa1.count + sa2.count, sa1.totalAmount.add(sa2.totalAmount))
                    )
                ));

            dailyGameSummaries.forEach((key, aggregate) -> {
                log.trace("ASYNC inserting DailyGameSalesSummary for date {}, game {}: {} sales, {} amount", key.date, key.gameNo, aggregate.count, aggregate.totalAmount);
                dailyGameSalesSummaryRepository.insertSummary(key.date, key.gameNo, aggregate.count, aggregate.totalAmount);
            });

            log.info("ASYNC aggregation completed for batch of {}. Time: {} ms", gameSalesBatch.size(), (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            log.error("ASYNC aggregation FAILED for a batch of {} sales. Error: {}", gameSalesBatch.size(), e.getMessage(), e);
        }
    }

    // Helper DTOs for aggregation
    private static class SalesAggregate {
        long count;
        BigDecimal totalAmount;
        SalesAggregate(long count, BigDecimal totalAmount) {
            this.count = count;
            this.totalAmount = totalAmount;
        }
    }

    private static class GameDateKey {
        LocalDate date;
        Integer gameNo;
        GameDateKey(LocalDate date, Integer gameNo) {
            this.date = date;
            this.gameNo = gameNo;
        }
       
        @Override public boolean equals(Object o) { /* ... */ return o instanceof GameDateKey && ((GameDateKey)o).date.equals(date) && ((GameDateKey)o).gameNo.equals(gameNo); }
        @Override public int hashCode() { /* ... */ return date.hashCode() * 31 + gameNo.hashCode(); }
    }
}