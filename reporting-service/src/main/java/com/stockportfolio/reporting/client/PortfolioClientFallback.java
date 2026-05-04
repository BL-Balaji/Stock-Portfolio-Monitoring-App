package com.stockportfolio.reporting.client;

import com.stockportfolio.reporting.dto.PortfolioData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PortfolioClientFallback implements PortfolioClient {

    @Override
    public List<PortfolioData> getPortfolios(Long userId) {
        return Collections.emptyList();
    }
}
