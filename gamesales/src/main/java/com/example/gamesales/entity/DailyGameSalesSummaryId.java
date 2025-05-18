package com.example.gamesales.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyGameSalesSummaryId implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocalDate summaryDate;
    private Integer gameNo;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyGameSalesSummaryId that = (DailyGameSalesSummaryId) o;
        return Objects.equals(summaryDate, that.summaryDate) &&
               Objects.equals(gameNo, that.gameNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaryDate, gameNo);
    }
}