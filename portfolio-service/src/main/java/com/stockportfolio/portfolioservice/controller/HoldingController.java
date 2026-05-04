package com.stockportfolio.portfolioservice.controller;

import com.stockportfolio.portfolioservice.dto.HoldingRequest;
import com.stockportfolio.portfolioservice.dto.HoldingResponse;
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

@RestController
@RequestMapping("/api/holdings")
@RequiredArgsConstructor
@Tag(name = "Holdings", description = "Manage stock holdings within portfolios")
@SecurityRequirement(name = "bearerAuth")
public class HoldingController {

    private final PortfolioService portfolioService;

    @Operation(
        summary = "Add a stock holding",
        description = "Adds a new stock holding to an existing portfolio. " +
                      "Provide `portfolioId`, `stockSymbol`, `quantity`, and `buyPrice`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Holding added successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HoldingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HoldingResponse> addHolding(@Valid @RequestBody HoldingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.addHolding(request));
    }

    @Operation(
        summary = "Update a holding",
        description = "Updates the quantity, buy price, or stock name of an existing holding."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Holding updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HoldingResponse.class))),
        @ApiResponse(responseCode = "404", description = "Holding not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<HoldingResponse> updateHolding(
            @Parameter(description = "Holding ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody HoldingRequest request) {
        return ResponseEntity.ok(portfolioService.updateHolding(id, request));
    }

    @Operation(summary = "Delete a holding", description = "Removes a stock holding from a portfolio.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Holding deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Holding not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHolding(
            @Parameter(description = "Holding ID", example = "1") @PathVariable Long id) {
        portfolioService.deleteHolding(id);
        return ResponseEntity.noContent().build();
    }
}
