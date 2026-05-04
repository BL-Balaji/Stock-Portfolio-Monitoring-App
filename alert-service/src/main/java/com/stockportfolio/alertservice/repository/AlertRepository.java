package com.stockportfolio.alertservice.repository;

import com.stockportfolio.alertservice.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByUserId(Long userId);

    List<Alert> findByStatus(Alert.AlertStatus status);

    List<Alert> findByStockSymbolAndStatus(String stockSymbol, Alert.AlertStatus status);
}
