package com.stockportfolio.alertservice.controller;

import com.stockportfolio.alertservice.dto.AlertRequest;
import com.stockportfolio.alertservice.dto.AlertResponse;
import com.stockportfolio.alertservice.entity.Alert;
import com.stockportfolio.alertservice.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
 * Alert controller - manages user-defined stock price and portfolio loss alerts.
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Configure and manage stock price and portfolio loss alerts")
@SecurityRequirement(name = "bearerAuth")
public class AlertController {

    private final AlertService alertService;

    @Operation(
        summary = "Get all alerts",
        description = "Returns all alerts for the authenticated user. Pass `X-User-Id` header."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerts returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AlertResponse.class))),
        @ApiResponse(responseCode = "400", description = "Missing X-User-Id header", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @Parameter(description = "User ID (injected by API Gateway from JWT)", example = "1")
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(alertService.getAlertsByUserId(userId));
    }

    @Operation(summary = "Get alert by ID", description = "Returns a specific alert by its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alert found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AlertResponse.class))),
        @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(
            @Parameter(description = "Alert ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @Operation(
        summary = "Create a new alert",
        description = "Creates a new price threshold or portfolio loss alert.\n\n" +
                      "**PRICE_THRESHOLD example**: Alert when AAPL goes ABOVE $200\n\n" +
                      "**PORTFOLIO_LOSS example**: Alert when portfolio loss exceeds 10%"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Alert configuration",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Price Threshold Alert",
                    value = "{\"alertType\":\"PRICE_THRESHOLD\",\"stockSymbol\":\"AAPL\",\"thresholdValue\":200.00,\"conditionType\":\"ABOVE\",\"notificationEmail\":\"user@example.com\"}"),
                @ExampleObject(name = "Portfolio Loss Alert",
                    value = "{\"alertType\":\"PORTFOLIO_LOSS\",\"thresholdValue\":10.00,\"notificationEmail\":\"user@example.com\"}")
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Alert created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AlertResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(
            @Valid @RequestBody AlertRequest request,
            @Parameter(description = "User ID (injected by API Gateway from JWT)", example = "1")
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.createAlert(request, userId));
    }

    @Operation(summary = "Update an alert", description = "Updates the configuration of an existing alert.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alert updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AlertResponse.class))),
        @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlertResponse> updateAlert(
            @Parameter(description = "Alert ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.updateAlert(id, request));
    }

    @Operation(summary = "Delete an alert", description = "Permanently deletes an alert.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Alert deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(
            @Parameter(description = "Alert ID", example = "1") @PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Toggle alert status",
        description = "Enable, disable, or reset an alert status.\n\n" +
                      "**Status values**: `ACTIVE`, `DISABLED`, `TRIGGERED`"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AlertResponse.class))),
        @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertResponse> toggleStatus(
            @Parameter(description = "Alert ID", example = "1") @PathVariable Long id,
            @Parameter(description = "New status", example = "ACTIVE",
                schema = @Schema(allowableValues = {"ACTIVE", "DISABLED", "TRIGGERED"}))
            @RequestParam Alert.AlertStatus status) {
        return ResponseEntity.ok(alertService.toggleAlertStatus(id, status));
    }
}
