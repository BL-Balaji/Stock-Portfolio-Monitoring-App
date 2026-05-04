package com.stockportfolio.pricefetcher.controller;

import com.stockportfolio.pricefetcher.dto.GainLossResponse;
import com.stockportfolio.pricefetcher.dto.StockPriceResponse;
import com.stockportfolio.pricefetcher.service.PriceFetcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Price Fetcher Controller - exposes stock price and gain/loss endpoints.
 */
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Tag(name = "Stock Prices", description = "Real-time stock price fetching and gain/loss calculation")
@SecurityRequirement(name = "bearerAuth")
public class PriceFetcherController {

    private final PriceFetcherService priceFetcherService;

    @Operation(
        summary = "Get current price (raw)",
        description = "Returns just the current price as a decimal number. Used internally by other services via Feign."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current price returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(type = "number", example = "185.50"))),
        @ApiResponse(responseCode = "500", description = "Failed to fetch price", content = @Content)
    })
    @GetMapping("/{symbol}/current")
    public ResponseEntity<BigDecimal> getCurrentPrice(
            @Parameter(description = "Stock symbol (e.g. AAPL, GOOGL, MSFT)", example = "AAPL")
            @PathVariable String symbol) {
        return ResponseEntity.ok(priceFetcherService.getCurrentPrice(symbol));
    }

    @Operation(
        summary = "Get full price details",
        description = "Returns complete price information including current price, previous close, daily change, and daily change percentage."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Price details returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = StockPriceResponse.class))),
        @ApiResponse(responseCode = "500", description = "Failed to fetch price", content = @Content)
    })
    @GetMapping("/{symbol}")
    public ResponseEntity<StockPriceResponse> getPriceDetails(
            @Parameter(description = "Stock symbol (e.g. AAPL, TSLA, NVDA)", example = "AAPL")
            @PathVariable String symbol) {
        return ResponseEntity.ok(priceFetcherService.getPriceDetails(symbol));
    }

    @Operation(
        summary = "Get all cached prices",
        description = "Returns all stock prices currently stored in the cache database."
    )
    @ApiResponse(responseCode = "200", description = "All cached prices returned",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StockPriceResponse.class)))
    @GetMapping
    public ResponseEntity<List<StockPriceResponse>> getAllPrices() {
        return ResponseEntity.ok(priceFetcherService.getAllCachedPrices());
    }

    @Operation(
        summary = "Force refresh price",
        description = "Manually triggers a price refresh for the given symbol from the external API (or mock). " +
                      "Returns the updated price details."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Price refreshed and returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = StockPriceResponse.class))),
        @ApiResponse(responseCode = "500", description = "Failed to refresh price", content = @Content)
    })
    @PostMapping("/{symbol}/refresh")
    public ResponseEntity<StockPriceResponse> refreshPrice(
            @Parameter(description = "Stock symbol to refresh", example = "AAPL")
            @PathVariable String symbol) {
        priceFetcherService.fetchAndCachePrice(symbol);
        return ResponseEntity.ok(priceFetcherService.getPriceDetails(symbol));
    }

    @Operation(
        summary = "Calculate gain/loss",
        description = "Calculates the gain or loss for a stock holding based on buy price, current price, and quantity.\n\n" +
                      "**Formula**: `gainLoss = (currentPrice - buyPrice) × quantity`\n\n" +
                      "**Percentage**: `gainLoss% = (gainLoss / investedValue) × 100`"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gain/loss calculated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GainLossResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content)
    })
    @GetMapping("/gainloss/{symbol}")
    public ResponseEntity<GainLossResponse> calculateGainLoss(
            @Parameter(description = "Stock symbol", example = "AAPL") @PathVariable String symbol,
            @Parameter(description = "Buy price per share", example = "150.00") @RequestParam BigDecimal buyPrice,
            @Parameter(description = "Number of shares", example = "10") @RequestParam Integer quantity) {
        return ResponseEntity.ok(priceFetcherService.calculateGainLoss(symbol, buyPrice, quantity));
    }
}
