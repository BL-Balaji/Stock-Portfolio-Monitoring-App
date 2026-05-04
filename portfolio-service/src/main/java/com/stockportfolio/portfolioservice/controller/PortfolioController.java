package com.stockportfolio.portfolioservice.controller;

import com.stockportfolio.portfolioservice.dto.HoldingResponse;
import com.stockportfolio.portfolioservice.dto.PortfolioRequest;
import com.stockportfolio.portfolioservice.dto.PortfolioResponse;
import com.stockportfolio.portfolioservice.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Portfolio controller - manages user portfolios.
 */
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * GET /api/portfolios
     * Get all portfolios for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosByUserId(userId));
    }

    /**
     * GET /api/portfolios/{id}
     * Get a specific portfolio by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getPortfolioById(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getPortfolioById(id));
    }

    /**
     * POST /api/portfolios
     * Create a new portfolio.
     */
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody PortfolioRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(request, userId));
    }

    /**
     * PUT /api/portfolios/{id}
     * Update an existing portfolio.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioRequest request) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, request));
    }

    /**
     * DELETE /api/portfolios/{id}
     * Delete a portfolio.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/portfolios/{id}/holdings
     * Get all holdings for a specific portfolio.
     */
    @GetMapping("/{id}/holdings")
    public ResponseEntity<List<HoldingResponse>> getHoldings(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getHoldingsByPortfolioId(id));
    }
}
