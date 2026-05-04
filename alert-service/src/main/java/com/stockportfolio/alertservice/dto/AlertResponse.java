package com.stockportfolio.alertservice.dto;

import com.stockportfolio.alertservice.entity.Alert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {

    private Long id;
    private Long userId;
    private Alert.AlertType alertType;
    private String stockSymbol;
    private BigDecimal thresholdValue;
    private Alert.ConditionType conditionType;
    private Alert.AlertStatus status;
    private String notificationEmail;
    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt;
}
