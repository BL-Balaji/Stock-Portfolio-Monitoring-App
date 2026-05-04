package com.stockportfolio.portfolioservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * Feign client to communicate with Price Fetcher Service.
 * Uses mock data if price-fetcher-service is unavailable.
 */
@FeignClient(name = "price-fetcher-service", fallback = PriceFetcherClientFallback.class)
public interface PriceFetcherClient {

    @GetMapping("/api/prices/{symbol}/current")
    BigDecimal getCurrentPrice(@PathVariable("symbol") String symbol);
}
