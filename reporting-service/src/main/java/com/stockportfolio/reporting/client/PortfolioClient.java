package com.stockportfolio.reporting.client;

import com.stockportfolio.reporting.dto.PortfolioData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Feign client to fetch portfolio data from Portfolio Service.
 */
@FeignClient(name = "portfolio-service", fallback = PortfolioClientFallback.class)
public interface PortfolioClient {

    @GetMapping("/api/portfolios")
    List<PortfolioData> getPortfolios(@RequestHeader("X-User-Id") Long userId);
}
