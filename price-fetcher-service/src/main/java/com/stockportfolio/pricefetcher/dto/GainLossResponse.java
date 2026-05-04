package com.stockportfolio.pricefetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GainLossResponse {

    private String stockSymbol;
    private BigDecimal buyPrice;
    private BigDecimal currentPrice;
    private Integer quantity;
    private BigDecimal investedValue;
    private BigDecimal currentValue;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;
}
