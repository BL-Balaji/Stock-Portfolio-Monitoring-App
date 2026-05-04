package com.stockportfolio.pricefetcher.controller;

import com.stockportfolio.pricefetcher.dto.GainLossResponse;
import com.stockportfolio.pricefetcher.dto.StockPriceResponse;
import com.stockportfolio.pricefetcher.service.PriceFetcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Price Fetcher Controller - exposes stock price and gain/loss endpoints.
 */
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceFetcherController {

    private final PriceFetcherService priceFetcherService;

    /**
     * GET /api/prices/{symbol}/current
     * Get current price for a stock symbol.
     */
    @GetMapping("/{symbol}/current")
    public ResponseEntity<BigDecimal> getCurrentPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(priceFetcherService.getCurrentPrice(symbol));
    }

    /**
     * GET /api/prices/{symbol}
     * Get full price details including daily change.
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<StockPriceResponse> getPriceDetails(@PathVariable String symbol) {
        return ResponseEntity.ok(priceFetcherService.getPriceDetails(symbol));
    }

    /**
     * GET /api/prices
     * Get all cached stock prices.
     */
    @GetMapping
    public ResponseEntity<List<StockPriceResponse>> getAllPrices() {
        return ResponseEntity.ok(priceFetcherService.getAllCachedPrices());
    }

    /**
     * POST /api/prices/{symbol}/refresh
     * Manually trigger a price refresh for a symbol.
     */
    @PostMapping("/{symbol}/refresh")
    public ResponseEntity<StockPriceResponse> refreshPrice(@PathVariable String symbol) {
        priceFetcherService.fetchAndCachePrice(symbol);
        return ResponseEntity.ok(priceFetcherService.getPriceDetails(symbol));
    }

    /**
     * GET /api/prices/gainloss/{symbol}
     * Calculate gain/loss for a holding.
     */
    @GetMapping("/gainloss/{symbol}")
    public ResponseEntity<GainLossResponse> calculateGainLoss(
            @PathVariable String symbol,
            @RequestParam BigDecimal buyPrice,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(priceFetcherService.calculateGainLoss(symbol, buyPrice, quantity));
    }
}
