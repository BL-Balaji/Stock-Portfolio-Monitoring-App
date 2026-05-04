package com.stockportfolio.reporting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PortfolioData {
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private List<HoldingData> holdings;
    private BigDecimal totalInvestedValue;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercentage;
    private LocalDateTime createdAt;
}
