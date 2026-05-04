package com.stockportfolio.portfolioservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Portfolio Service - Manages user portfolios and stock holdings.
 * Communicates with Price Fetcher Service to get real-time values.
 * Runs on port 8082.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PortfolioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioServiceApplication.class, args);
    }
}
