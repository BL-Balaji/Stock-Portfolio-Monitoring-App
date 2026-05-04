package com.stockportfolio.pricefetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceResponse {

    private String stockSymbol;
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal dailyChange;
    private BigDecimal dailyChangePercent;
    private LocalDateTime lastUpdated;
}
