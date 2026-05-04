package com.stockportfolio.pricefetcher.repository;

import com.stockportfolio.pricefetcher.entity.StockPriceCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockPriceCacheRepository extends JpaRepository<StockPriceCache, Long> {

    Optional<StockPriceCache> findByStockSymbol(String stockSymbol);
}
