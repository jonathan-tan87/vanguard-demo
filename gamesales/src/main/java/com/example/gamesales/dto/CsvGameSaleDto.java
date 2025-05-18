package com.example.gamesales.dto;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CsvGameSaleDto {
    @CsvBindByPosition(position = 0)
    private String idStr;

    @CsvBindByPosition(position = 1)
    private String gameNoStr;

    @CsvBindByPosition(position = 2)
    @Size(max = 20, message = "Game name must not exceed 20 characters")
    private String gameName;

    @CsvBindByPosition(position = 3)
    @Size(max = 5, message = "Game code must not exceed 5 characters")
    private String gameCode;

    @CsvBindByPosition(position = 4)
    private String typeStr;

    @CsvBindByPosition(position = 5)
    private String costPriceStr;

    @CsvBindByPosition(position = 6)
    private String taxStr;

    @CsvBindByPosition(position = 7)
    private String salePriceStr;

    @CsvBindByPosition(position = 8)
    private String dateOfSaleStr;

    public transient Integer id;
    @Min(value = 1, message = "Game No must be between 1 and 100")
    @Max(value = 100, message = "Game No must be between 1 and 100")
    public transient Integer gameNo;
    @Min(value = 1, message = "Type must be 1 (Online) or 2 (Offline)")
    @Max(value = 2, message = "Type must be 1 (Online) or 2 (Offline)")
    public transient Integer type;
    @DecimalMax(value = "100.00", message = "Cost price must not exceed 100")
    @Digits(integer = 3, fraction = 2)
    public transient BigDecimal costPrice;
    @Digits(integer = 10, fraction = 2)
    public transient BigDecimal tax;
    @Digits(integer = 10, fraction = 2)
    public transient BigDecimal salePrice;
    public transient java.sql.Timestamp dateOfSale;
}