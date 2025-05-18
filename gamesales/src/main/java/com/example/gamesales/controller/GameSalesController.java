package com.example.gamesales.controller;

import com.example.gamesales.dto.GameSaleResponseDto;
import com.example.gamesales.dto.TotalSalesDailyByGameDto;
import com.example.gamesales.dto.TotalSalesDailyDto;
import com.example.gamesales.repository.DailyGameSalesSummaryRepository;
import com.example.gamesales.repository.DailySalesSummaryRepository;
import com.example.gamesales.service.GameSalesQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameSalesController {

    private final GameSalesQueryService gameSalesQueryService;
    private final DailySalesSummaryRepository dailySalesSummaryRepository;
    private final DailyGameSalesSummaryRepository dailyGameSalesSummaryRepository;

    @GetMapping("/getGameSales")
    public ResponseEntity<Page<GameSaleResponseDto>> getGameSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) BigDecimal priceLessThan,
            @RequestParam(required = false) BigDecimal priceGreaterThan,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        long startTime = System.nanoTime();
        System.out.println("in getGameSales, fromDate = " + fromDate + " toDate = " + toDate);
        Page<GameSaleResponseDto> result = gameSalesQueryService.getGameSales(
                fromDate, toDate, priceLessThan, priceGreaterThan, page, size);
        long duration = (System.nanoTime() - startTime) / 1_000_000;
        System.out.println("/getGameSales query took: " + duration + "ms");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getTotalSales")
    public ResponseEntity<?> getTotalSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer gameNo) {

        long startTime = System.nanoTime();
        List<?> result;

        if (gameNo != null) {
            result = dailyGameSalesSummaryRepository
                    .findBySummaryDateBetweenAndGameNoOrderBySummaryDateAsc(fromDate, toDate, gameNo)
                    .stream()
                    .map(TotalSalesDailyByGameDto::fromEntity)
                    .collect(Collectors.toList());
        } else {
            result = dailySalesSummaryRepository
                    .findBySummaryDateBetweenOrderBySummaryDateAsc(fromDate, toDate)
                    .stream()
                    .map(TotalSalesDailyDto::fromEntity)
                    .collect(Collectors.toList());
        }
        long duration = (System.nanoTime() - startTime) / 1_000_000;
        System.out.println("/getTotalSales query took: " + duration + "ms");

        return ResponseEntity.ok(result);
    }
}