package com.stockportfolio.portfolioservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for creating or updating a portfolio")
public class PortfolioRequest {

    @Schema(description = "Portfolio name", example = "My Tech Portfolio")
    @NotBlank(message = "Portfolio name is required")
    private String name;

    @Schema(description = "Optional description", example = "Long-term tech stock investments")
    private String description;
}
