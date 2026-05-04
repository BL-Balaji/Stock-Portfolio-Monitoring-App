package com.stockportfolio.alertservice.dto;

import com.stockportfolio.alertservice.entity.Alert;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request body for creating or updating an alert")
public class AlertRequest {

    @Schema(description = "Type of alert", example = "PRICE_THRESHOLD",
            allowableValues = {"PRICE_THRESHOLD", "PORTFOLIO_LOSS"})
    @NotNull(message = "Alert type is required")
    private Alert.AlertType alertType;

    @Schema(description = "Stock symbol (required for PRICE_THRESHOLD alerts)", example = "AAPL")
    private String stockSymbol;

    @Schema(description = "Threshold value — price for PRICE_THRESHOLD, percentage for PORTFOLIO_LOSS",
            example = "200.00")
    @Positive(message = "Threshold value must be positive")
    private BigDecimal thresholdValue;

    @Schema(description = "Condition direction (required for PRICE_THRESHOLD)", example = "ABOVE",
            allowableValues = {"ABOVE", "BELOW"})
    private Alert.ConditionType conditionType;

    @Schema(description = "Email to notify when alert triggers (optional)", example = "user@example.com")
    private String notificationEmail;
}
