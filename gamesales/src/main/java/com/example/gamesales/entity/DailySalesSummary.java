package com.example.gamesales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_sales_summary", indexes = {
    @Index(name = "idx_dss_summary_date", columnList = "summary_date")
})
@Data
@NoArgsConstructor
public class DailySalesSummary {
    @Id
    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(name = "total_games_sold", nullable = false)
    private Long totalGamesSold = 0L;

    @Column(name = "total_sales_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalSalesAmount = BigDecimal.ZERO;

    public DailySalesSummary(LocalDate summaryDate) {
        this.summaryDate = summaryDate;
    }
}