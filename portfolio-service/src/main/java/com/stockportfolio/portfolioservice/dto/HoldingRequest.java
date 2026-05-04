package com.stockportfolio.portfolioservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request body for adding or updating a stock holding")
public class HoldingRequest {

    @Schema(description = "Stock ticker symbol", example = "AAPL")
    @NotBlank(message = "Stock symbol is required")
    private String stockSymbol;

    @Schema(description = "Company name (optional)", example = "Apple Inc.")
    private String stockName;

    @Schema(description = "Number of shares purchased", example = "10")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Schema(description = "Price per share at time of purchase", example = "150.00")
    @NotNull(message = "Buy price is required")
    @Positive(message = "Buy price must be positive")
    private BigDecimal buyPrice;

    @Schema(description = "ID of the portfolio to add this holding to", example = "1")
    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;
}
