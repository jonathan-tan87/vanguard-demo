package com.example.gamesales.dto;

import com.example.gamesales.entity.DailySalesSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalSalesDailyDto {
    private LocalDate date;
    private Long totalGamesSold;
    private BigDecimal totalSalesAmount;

    public static TotalSalesDailyDto fromEntity(DailySalesSummary summary) {
        return new TotalSalesDailyDto(
            summary.getSummaryDate(),
            summary.getTotalGamesSold(),
            summary.getTotalSalesAmount()
        );
    }
}