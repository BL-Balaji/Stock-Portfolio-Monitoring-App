package com.stockportfolio.alertservice.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceFetcherClientFallback implements PriceFetcherClient {

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        return BigDecimal.ZERO;
    }
}
