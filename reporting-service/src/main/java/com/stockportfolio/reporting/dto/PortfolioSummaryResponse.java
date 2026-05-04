package com.stockportfolio.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryResponse {

    private Long userId;
    private int totalPortfolios;
    private int totalHoldings;
    private BigDecimal totalInvestedValue;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercentage;
    private List<PortfolioData> portfolios;
    private LocalDateTime generatedAt;
}
