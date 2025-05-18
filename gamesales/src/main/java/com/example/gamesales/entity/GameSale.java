package com.example.gamesales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "game_sales", indexes = {
    @Index(name = "idx_date_of_sale", columnList = "date_of_sale"),
    @Index(name = "idx_sale_price", columnList = "sale_price"),
    @Index(name = "idx_game_no", columnList = "game_no"),
    @Index(name = "idx_date_game_no", columnList = "date_of_sale, game_no") 
})
@Data
@NoArgsConstructor
public class GameSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "csv_row_id")
    private Integer csvRowId;

    @NotNull
    @Min(1)
    @Max(100)
    @Column(name = "game_no", nullable = false)
    private Integer gameNo;

    @NotBlank
    @Size(max = 20)
    @Column(name = "game_name", length = 20, nullable = false)
    private String gameName;

    @NotBlank
    @Size(max = 5)
    @Column(name = "game_code", length = 5, nullable = false)
    private String gameCode;

    @NotNull
    @Min(1)
    @Max(2) // 1 = Online, 2 = Offline
    @Column(name = "type", nullable = false)
    private Integer type;

    @NotNull
    @DecimalMax("100.00")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "cost_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal costPrice;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Column(name = "tax", precision = 10, scale = 2, nullable = false)
    private BigDecimal tax;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Column(name = "sale_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal salePrice;

    @NotNull
    @Column(name = "date_of_sale", nullable = false)
    private Timestamp dateOfSale;
}