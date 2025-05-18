package com.example.gamesales.dto;

import com.example.gamesales.entity.DailyGameSalesSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalSalesDailyByGameDto {
    private LocalDate date;
    private Integer gameNo;
    private Long totalGamesSold;
    private BigDecimal totalSalesAmount;

     public static TotalSalesDailyByGameDto fromEntity(DailyGameSalesSummary summary) {
        return new TotalSalesDailyByGameDto(
            summary.getSummaryDate(),
            summary.getGameNo(),
            summary.getTotalGamesSold(),
            summary.getTotalSalesAmount()
        );
    }
}