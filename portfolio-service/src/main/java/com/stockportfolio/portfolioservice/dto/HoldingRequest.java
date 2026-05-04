package com.stockportfolio.portfolioservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HoldingRequest {

    @NotBlank(message = "Stock symbol is required")
    private String stockSymbol;

    private String stockName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Buy price is required")
    @Positive(message = "Buy price must be positive")
    private BigDecimal buyPrice;

    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;
}
