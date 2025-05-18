package com.example.gamesales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_game_sales_summary",
       uniqueConstraints = @UniqueConstraint(columnNames = {"summary_date", "game_no"}),
       indexes = {
           @Index(name = "idx_dgss_summary_date_game_no", columnList = "summary_date, game_no")
       })
@Data
@NoArgsConstructor
@IdClass(DailyGameSalesSummaryId.class)
public class DailyGameSalesSummary {

    @Id
    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Id
    @Column(name = "game_no", nullable = false)
    private Integer gameNo;

    @Column(name = "total_games_sold", nullable = false)
    private Long totalGamesSold = 0L;

    @Column(name = "total_sales_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalSalesAmount = BigDecimal.ZERO;

    public DailyGameSalesSummary(LocalDate summaryDate, Integer gameNo) {
        this.summaryDate = summaryDate;
        this.gameNo = gameNo;
    }
}