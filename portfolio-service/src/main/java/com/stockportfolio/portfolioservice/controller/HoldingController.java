package com.stockportfolio.portfolioservice.controller;

import com.stockportfolio.portfolioservice.dto.HoldingRequest;
import com.stockportfolio.portfolioservice.dto.HoldingResponse;
import com.stockportfolio.portfolioservice.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/holdings")
@RequiredArgsConstructor
public class HoldingController {

    private final PortfolioService portfolioService;

    /** POST /api/holdings - Add a new stock holding to a portfolio. */
    @PostMapping
    public ResponseEntity<HoldingResponse> addHolding(@Valid @RequestBody HoldingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.addHolding(request));
    }

    /** PUT /api/holdings/{id} - Update an existing holding. */
    @PutMapping("/{id}")
    public ResponseEntity<HoldingResponse> updateHolding(
            @PathVariable Long id,
            @Valid @RequestBody HoldingRequest request) {
        return ResponseEntity.ok(portfolioService.updateHolding(id, request));
    }

    /** DELETE /api/holdings/{id} - Remove a holding from a portfolio. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHolding(@PathVariable Long id) {
        portfolioService.deleteHolding(id);
        return ResponseEntity.noContent().build();
    }
}
