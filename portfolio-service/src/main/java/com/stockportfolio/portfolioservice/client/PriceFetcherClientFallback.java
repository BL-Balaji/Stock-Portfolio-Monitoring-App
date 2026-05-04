package com.stockportfolio.portfolioservice.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Fallback implementation for PriceFetcherClient.
 * Returns mock data when price-fetcher-service is unavailable.
 */
@Component
public class PriceFetcherClientFallback implements PriceFetcherClient {

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        // Return mock price as fallback - integration happens when both services are ready
        return BigDecimal.ZERO;
    }
}
