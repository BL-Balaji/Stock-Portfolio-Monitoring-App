package com.stockportfolio.pricefetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Price Fetcher Service - Fetches real-time stock prices from 3rd-party APIs,
 * caches results, and provides Gain/Loss calculations.
 * Runs on port 8083.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
public class PriceFetcherServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceFetcherServiceApplication.class, args);
    }
}
