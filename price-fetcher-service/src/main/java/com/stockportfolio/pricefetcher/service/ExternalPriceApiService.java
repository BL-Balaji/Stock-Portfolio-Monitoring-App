package com.stockportfolio.pricefetcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;

/**
 * Service to fetch stock prices from external APIs.
 * Supports TwelveData API with fallback to mock data for development.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPriceApiService {

    private final RestTemplate restTemplate;

    @Value("${external.api.twelvedata.key:demo}")
    private String twelveDataApiKey;

    @Value("${external.api.twelvedata.url:https://api.twelvedata.com}")
    private String twelveDataBaseUrl;

    @Value("${external.api.use-mock:true}")
    private boolean useMock;

    /**
     * Fetch current price for a stock symbol.
     * Uses TwelveData API or falls back to mock data.
     */
    public BigDecimal fetchCurrentPrice(String symbol) {
        if (useMock) {
            return getMockPrice(symbol);
        }

        try {
            String url = String.format("%s/price?symbol=%s&apikey=%s",
                    twelveDataBaseUrl, symbol, twelveDataApiKey);

            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("price")) {
                return new BigDecimal(response.get("price").toString())
                        .setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch price for {} from external API, using mock: {}", symbol, e.getMessage());
        }

        return getMockPrice(symbol);
    }

    /**
     * Fetch previous close price for daily change calculation.
     */
    public BigDecimal fetchPreviousClose(String symbol) {
        if (useMock) {
            return getMockPrice(symbol).multiply(BigDecimal.valueOf(0.98));
        }

        try {
            String url = String.format("%s/eod?symbol=%s&apikey=%s",
                    twelveDataBaseUrl, symbol, twelveDataApiKey);

            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("close")) {
                return new BigDecimal(response.get("close").toString())
                        .setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch previous close for {}: {}", symbol, e.getMessage());
        }

        return getMockPrice(symbol).multiply(BigDecimal.valueOf(0.98));
    }

    /**
     * Generate realistic mock prices for development/testing.
     * Prices are seeded by symbol for consistency within a session.
     */
    private BigDecimal getMockPrice(String symbol) {
        // Seed-based mock prices for common symbols
        Map<String, Double> mockPrices = Map.of(
                "AAPL", 185.50,
                "GOOGL", 175.20,
                "MSFT", 420.30,
                "AMZN", 195.80,
                "TSLA", 175.60,
                "META", 510.40,
                "NVDA", 875.20,
                "NFLX", 650.10
        );

        double basePrice = mockPrices.getOrDefault(symbol.toUpperCase(),
                100.0 + (symbol.hashCode() % 400));

        // Add small random variation to simulate live prices
        double variation = (new Random(System.currentTimeMillis() / 60000).nextDouble() - 0.5) * 2;
        double price = basePrice + variation;

        return BigDecimal.valueOf(Math.max(price, 1.0)).setScale(2, RoundingMode.HALF_UP);
    }
}
