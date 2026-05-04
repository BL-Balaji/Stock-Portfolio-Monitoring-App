package com.stockportfolio.alertservice.service;

import com.stockportfolio.alertservice.client.PriceFetcherClient;
import com.stockportfolio.alertservice.dto.AlertRequest;
import com.stockportfolio.alertservice.dto.AlertResponse;
import com.stockportfolio.alertservice.entity.Alert;
import com.stockportfolio.alertservice.exception.ResourceNotFoundException;
import com.stockportfolio.alertservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Alert service - manages alert CRUD and evaluates alert conditions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final PriceFetcherClient priceFetcherClient;
    private final NotificationService notificationService;

    public List<AlertResponse> getAlertsByUserId(Long userId) {
        return alertRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AlertResponse getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
        return mapToResponse(alert);
    }

    @Transactional
    public AlertResponse createAlert(AlertRequest request, Long userId) {
        Alert alert = Alert.builder()
                .userId(userId)
                .alertType(request.getAlertType())
                .stockSymbol(request.getStockSymbol() != null
                        ? request.getStockSymbol().toUpperCase() : null)
                .thresholdValue(request.getThresholdValue())
                .conditionType(request.getConditionType())
                .notificationEmail(request.getNotificationEmail())
                .status(Alert.AlertStatus.ACTIVE)
                .build();

        return mapToResponse(alertRepository.save(alert));
    }

    @Transactional
    public AlertResponse updateAlert(Long id, AlertRequest request) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));

        alert.setAlertType(request.getAlertType());
        alert.setStockSymbol(request.getStockSymbol() != null
                ? request.getStockSymbol().toUpperCase() : null);
        alert.setThresholdValue(request.getThresholdValue());
        alert.setConditionType(request.getConditionType());
        alert.setNotificationEmail(request.getNotificationEmail());

        return mapToResponse(alertRepository.save(alert));
    }

    @Transactional
    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
    }

    @Transactional
    public AlertResponse toggleAlertStatus(Long id, Alert.AlertStatus status) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
        alert.setStatus(status);
        return mapToResponse(alertRepository.save(alert));
    }

    /**
     * Scheduled alert evaluation - runs every 5 minutes.
     * Compares current prices with thresholds for all active alerts.
     */
    @Scheduled(fixedRateString = "${alert.evaluation.interval:300000}")
    @Transactional
    public void evaluateAlerts() {
        List<Alert> activeAlerts = alertRepository.findByStatus(Alert.AlertStatus.ACTIVE);
        if (activeAlerts.isEmpty()) return;

        log.info("Evaluating {} active alerts...", activeAlerts.size());

        for (Alert alert : activeAlerts) {
            try {
                evaluateSingleAlert(alert);
            } catch (Exception e) {
                log.error("Error evaluating alert {}: {}", alert.getId(), e.getMessage());
            }
        }
    }

    private void evaluateSingleAlert(Alert alert) {
        if (alert.getAlertType() == Alert.AlertType.PRICE_THRESHOLD
                && alert.getStockSymbol() != null) {

            BigDecimal currentPrice = priceFetcherClient.getCurrentPrice(alert.getStockSymbol());
            if (currentPrice.compareTo(BigDecimal.ZERO) == 0) return;

            boolean conditionMet = switch (alert.getConditionType()) {
                case ABOVE -> currentPrice.compareTo(alert.getThresholdValue()) > 0;
                case BELOW -> currentPrice.compareTo(alert.getThresholdValue()) < 0;
            };

            if (conditionMet) {
                triggerAlert(alert, currentPrice);
            }
        }
    }

    private void triggerAlert(Alert alert, BigDecimal currentPrice) {
        alert.setStatus(Alert.AlertStatus.TRIGGERED);
        alert.setTriggeredAt(LocalDateTime.now());
        alertRepository.save(alert);

        notificationService.sendAlertNotification(alert, currentPrice);
        log.info("Alert {} triggered for symbol {} at price {}",
                alert.getId(), alert.getStockSymbol(), currentPrice);
    }

    private AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .userId(alert.getUserId())
                .alertType(alert.getAlertType())
                .stockSymbol(alert.getStockSymbol())
                .thresholdValue(alert.getThresholdValue())
                .conditionType(alert.getConditionType())
                .status(alert.getStatus())
                .notificationEmail(alert.getNotificationEmail())
                .triggeredAt(alert.getTriggeredAt())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
