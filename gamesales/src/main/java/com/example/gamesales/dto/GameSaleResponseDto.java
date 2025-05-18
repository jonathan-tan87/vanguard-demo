package com.example.gamesales.dto;

import com.example.gamesales.entity.GameSale;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class GameSaleResponseDto {
    private Long id;
    private Integer gameNo;
    private String gameName;
    private String gameCode;
    private Integer type;
    private BigDecimal costPrice;
    private BigDecimal tax;
    private BigDecimal salePrice;
    private Timestamp dateOfSale;

    public static GameSaleResponseDto fromEntity(GameSale gameSale) {
        GameSaleResponseDto dto = new GameSaleResponseDto();
        dto.setId(gameSale.getId());
        dto.setGameNo(gameSale.getGameNo());
        dto.setGameName(gameSale.getGameName());
        dto.setGameCode(gameSale.getGameCode());
        dto.setType(gameSale.getType());
        dto.setCostPrice(gameSale.getCostPrice());
        dto.setTax(gameSale.getTax());
        dto.setSalePrice(gameSale.getSalePrice());
        dto.setDateOfSale(gameSale.getDateOfSale());
        return dto;
    }
}