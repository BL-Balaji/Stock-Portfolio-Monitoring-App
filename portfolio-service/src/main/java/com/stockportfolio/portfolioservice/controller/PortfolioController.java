package com.stockportfolio.portfolioservice.controller;

import com.stockportfolio.portfolioservice.dto.HoldingResponse;
import com.stockportfolio.portfolioservice.dto.PortfolioRequest;
import com.stockportfolio.portfolioservice.dto.PortfolioResponse;
import com.stockportfolio.portfolioservice.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Portfolio controller - manages user portfolios.
 */
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolios", description = "Create and manage stock portfolios — pass X-User-Id header")
@SecurityRequirement(name = "bearerAuth")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(
        summary = "Get all portfolios",
        description = "Returns all portfolios for the authenticated user. Pass `X-User-Id` header with the user's ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Portfolios returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Missing X-User-Id header", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(
            @Parameter(description = "User ID (injected by API Gateway from JWT)", example = "1")
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosByUserId(userId));
    }

    @Operation(summary = "Get portfolio by ID", description = "Returns a specific portfolio with all holdings and gain/loss data.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Portfolio found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getPortfolioById(
            @Parameter(description = "Portfolio ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getPortfolioById(id));
    }

    @Operation(
        summary = "Create a new portfolio",
        description = "Creates a new portfolio for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Portfolio created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody PortfolioRequest request,
            @Parameter(description = "User ID (injected by API Gateway from JWT)", example = "1")
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(request, userId));
    }

    @Operation(summary = "Update portfolio", description = "Updates the name and description of an existing portfolio.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Portfolio updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(
            @Parameter(description = "Portfolio ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PortfolioRequest request) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, request));
    }

    @Operation(summary = "Delete portfolio", description = "Permanently deletes a portfolio and all its holdings.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Portfolio deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(
            @Parameter(description = "Portfolio ID", example = "1") @PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get holdings for a portfolio", description = "Returns all stock holdings in a portfolio with current prices and gain/loss.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Holdings returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HoldingResponse.class))),
        @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content)
    })
    @GetMapping("/{id}/holdings")
    public ResponseEntity<List<HoldingResponse>> getHoldings(
            @Parameter(description = "Portfolio ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getHoldingsByPortfolioId(id));
    }
}
