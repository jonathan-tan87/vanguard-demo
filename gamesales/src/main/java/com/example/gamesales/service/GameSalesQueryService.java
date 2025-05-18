package com.example.gamesales.service;

import com.example.gamesales.dto.GameSaleResponseDto;
import com.example.gamesales.entity.GameSale;
import com.example.gamesales.repository.GameSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameSalesQueryService {

    private final GameSaleRepository gameSaleRepository;
    private static final int DEFAULT_PAGE_SIZE = 100;

    public Page<GameSaleResponseDto> getGameSales(
            LocalDate fromDate, LocalDate toDate,
            BigDecimal priceLessThan, BigDecimal priceGreaterThan,
            Integer page, Integer size) {

        Pageable pageable = PageRequest.of(
            page == null ? 0 : page,
            size == null ? DEFAULT_PAGE_SIZE : size,
            Sort.by("id").ascending()
        );

        Page<GameSale> gameSalesPage;

        if (fromDate != null && toDate != null) {
            Timestamp fromTimestamp = Timestamp.valueOf(fromDate.atStartOfDay());
            Timestamp toTimestamp = Timestamp.valueOf(toDate.atTime(LocalTime.MAX));
            gameSalesPage = gameSaleRepository.findByDateOfSaleBetween(fromTimestamp, toTimestamp, pageable);
        } else if (priceLessThan != null) {
            gameSalesPage = gameSaleRepository.findBySalePriceLessThan(priceLessThan, pageable);
        } else if (priceGreaterThan != null) {
            gameSalesPage = gameSaleRepository.findBySalePriceGreaterThan(priceGreaterThan, pageable);
        } else {
            gameSalesPage = gameSaleRepository.findAll(pageable);
        }
        return gameSalesPage.map(GameSaleResponseDto::fromEntity);
    }
}