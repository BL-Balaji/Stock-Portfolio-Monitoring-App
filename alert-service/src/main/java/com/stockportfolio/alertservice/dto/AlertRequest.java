package com.stockportfolio.alertservice.dto;

import com.stockportfolio.alertservice.entity.Alert;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertRequest {

    @NotNull(message = "Alert type is required")
    private Alert.AlertType alertType;

    private String stockSymbol;

    @Positive(message = "Threshold value must be positive")
    private BigDecimal thresholdValue;

    private Alert.ConditionType conditionType;

    private String notificationEmail;
}
