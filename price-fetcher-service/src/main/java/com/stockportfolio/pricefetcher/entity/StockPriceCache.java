package com.stockportfolio.pricefetcher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persisted cache for stock prices fetched from external APIs.
 */
@Entity
@Table(name = "stock_price_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_symbol", nullable = false, unique = true)
    private String stockSymbol;

    @Column(name = "current_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "previous_close", precision = 15, scale = 2)
    private BigDecimal previousClose;

    @Column(name = "daily_change", precision = 15, scale = 2)
    private BigDecimal dailyChange;

    @Column(name = "daily_change_percent", precision = 8, scale = 4)
    private BigDecimal dailyChangePercent;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
