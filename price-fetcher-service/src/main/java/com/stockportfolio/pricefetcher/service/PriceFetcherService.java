package com.stockportfolio.pricefetcher.service;

import com.stockportfolio.pricefetcher.dto.GainLossResponse;
import com.stockportfolio.pricefetcher.dto.StockPriceResponse;
import com.stockportfolio.pricefetcher.entity.StockPriceCache;
import com.stockportfolio.pricefetcher.repository.StockPriceCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service for fetching, caching, and providing stock prices.
 * Also handles Gain/Loss calculations per the business logic spec.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PriceFetcherService {

    private final StockPriceCacheRepository cacheRepository;
    private final ExternalPriceApiService externalPriceApiService;

    /**
     * Get current price for a symbol (from cache or fetch fresh).
     */
    @Cacheable(value = "stockPrices", key = "#symbol")
    public BigDecimal getCurrentPrice(String symbol) {
        return cacheRepository.findByStockSymbol(symbol.toUpperCase())
                .map(StockPriceCache::getCurrentPrice)
                .orElseGet(() -> fetchAndCachePrice(symbol));
    }

    /**
     * Get full price details for a symbol.
     */
    public StockPriceResponse getPriceDetails(String symbol) {
        StockPriceCache cache = cacheRepository.findByStockSymbol(symbol.toUpperCase())
                .orElseGet(() -> {
                    fetchAndCachePrice(symbol);
                    return cacheRepository.findByStockSymbol(symbol.toUpperCase()).orElseThrow();
                });

        return mapToResponse(cache);
    }

    /**
     * Get all cached prices.
     */
    public List<StockPriceResponse> getAllCachedPrices() {
        return cacheRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate gain/loss for a specific holding.
     * Formula: gain = (currentPrice - buyPrice) * quantity
     *          percentage = (gain / (buyPrice * quantity)) * 100
     */
    public GainLossResponse calculateGainLoss(String symbol, BigDecimal buyPrice, Integer quantity) {
        BigDecimal currentPrice = getCurrentPrice(symbol);
        BigDecimal investedValue = buyPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal gainLoss = currentValue.subtract(investedValue);
        BigDecimal gainLossPct = investedValue.compareTo(BigDecimal.ZERO) != 0
                ? gainLoss.divide(investedValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return GainLossResponse.builder()
                .stockSymbol(symbol.toUpperCase())
                .buyPrice(buyPrice)
                .currentPrice(currentPrice)
                .quantity(quantity)
                .investedValue(investedValue.setScale(2, RoundingMode.HALF_UP))
                .currentValue(currentValue.setScale(2, RoundingMode.HALF_UP))
                .gainLoss(gainLoss.setScale(2, RoundingMode.HALF_UP))
                .gainLossPercentage(gainLossPct.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    /**
     * Scheduled job: refresh all cached stock prices every 5 minutes.
     */
    @Scheduled(fixedRateString = "${price.fetch.interval:300000}")
    @Transactional
    public void refreshAllPrices() {
        List<StockPriceCache> allCached = cacheRepository.findAll();
        if (allCached.isEmpty()) {
            log.info("No symbols in cache to refresh.");
            return;
        }
        log.info("Refreshing prices for {} symbols...", allCached.size());
        allCached.forEach(cache -> fetchAndCachePrice(cache.getStockSymbol()));
        log.info("Price refresh complete.");
    }

    /**
     * Manually trigger a price refresh for a specific symbol.
     */
    @CachePut(value = "stockPrices", key = "#symbol")
    @Transactional
    public BigDecimal fetchAndCachePrice(String symbol) {
        String upperSymbol = symbol.toUpperCase();
        BigDecimal currentPrice = externalPriceApiService.fetchCurrentPrice(upperSymbol);
        BigDecimal previousClose = externalPriceApiService.fetchPreviousClose(upperSymbol);
        BigDecimal dailyChange = currentPrice.subtract(previousClose);
        BigDecimal dailyChangePct = previousClose.compareTo(BigDecimal.ZERO) != 0
                ? dailyChange.divide(previousClose, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        StockPriceCache cache = cacheRepository.findByStockSymbol(upperSymbol)
                .orElse(StockPriceCache.builder().stockSymbol(upperSymbol).build());

        cache.setCurrentPrice(currentPrice);
        cache.setPreviousClose(previousClose);
        cache.setDailyChange(dailyChange.setScale(2, RoundingMode.HALF_UP));
        cache.setDailyChangePercent(dailyChangePct.setScale(4, RoundingMode.HALF_UP));

        cacheRepository.save(cache);
        log.debug("Cached price for {}: {}", upperSymbol, currentPrice);
        return currentPrice;
    }

    private StockPriceResponse mapToResponse(StockPriceCache cache) {
        return StockPriceResponse.builder()
                .stockSymbol(cache.getStockSymbol())
                .currentPrice(cache.getCurrentPrice())
                .previousClose(cache.getPreviousClose())
                .dailyChange(cache.getDailyChange())
                .dailyChangePercent(cache.getDailyChangePercent())
                .lastUpdated(cache.getLastUpdated())
                .build();
    }
}
