package com.stockportfolio.reporting.service;

import com.stockportfolio.reporting.client.PortfolioClient;
import com.stockportfolio.reporting.dto.PortfolioData;
import com.stockportfolio.reporting.dto.PortfolioSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reporting service - aggregates portfolio data and generates reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final PortfolioClient portfolioClient;

    public PortfolioSummaryResponse getPortfolioSummary(Long userId) {
        List<PortfolioData> portfolios = portfolioClient.getPortfolios(userId);

        BigDecimal totalInvested = portfolios.stream()
                .map(p -> p.getTotalInvestedValue() != null ? p.getTotalInvestedValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrent = portfolios.stream()
                .map(p -> p.getTotalCurrentValue() != null ? p.getTotalCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGainLoss = totalCurrent.subtract(totalInvested);
        BigDecimal totalGainLossPct = totalInvested.compareTo(BigDecimal.ZERO) != 0
                ? totalGainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        int totalHoldings = portfolios.stream()
                .mapToInt(p -> p.getHoldings() != null ? p.getHoldings().size() : 0)
                .sum();

        return PortfolioSummaryResponse.builder()
                .userId(userId)
                .totalPortfolios(portfolios.size())
                .totalHoldings(totalHoldings)
                .totalInvestedValue(totalInvested.setScale(2, RoundingMode.HALF_UP))
                .totalCurrentValue(totalCurrent.setScale(2, RoundingMode.HALF_UP))
                .totalGainLoss(totalGainLoss.setScale(2, RoundingMode.HALF_UP))
                .totalGainLossPercentage(totalGainLossPct.setScale(2, RoundingMode.HALF_UP))
                .portfolios(portfolios)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
