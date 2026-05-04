package com.stockportfolio.reporting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HoldingData {
    private Long id;
    private String stockSymbol;
    private String stockName;
    private Integer quantity;
    private BigDecimal buyPrice;
    private BigDecimal currentPrice;
    private BigDecimal investedValue;
    private BigDecimal currentValue;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;
}
