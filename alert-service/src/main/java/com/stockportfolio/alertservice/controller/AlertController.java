package com.stockportfolio.alertservice.controller;

import com.stockportfolio.alertservice.dto.AlertRequest;
import com.stockportfolio.alertservice.dto.AlertResponse;
import com.stockportfolio.alertservice.entity.Alert;
import com.stockportfolio.alertservice.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Alert controller - manages user-defined stock price and portfolio loss alerts.
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * GET /api/alerts
     * Get all alerts for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(alertService.getAlertsByUserId(userId));
    }

    /**
     * GET /api/alerts/{id}
     * Get a specific alert by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    /**
     * POST /api/alerts
     * Create a new alert.
     */
    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(
            @Valid @RequestBody AlertRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.createAlert(request, userId));
    }

    /**
     * PUT /api/alerts/{id}
     * Update an existing alert.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlertResponse> updateAlert(
            @PathVariable Long id,
            @Valid @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.updateAlert(id, request));
    }

    /**
     * DELETE /api/alerts/{id}
     * Delete an alert.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/alerts/{id}/status
     * Enable or disable an alert.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertResponse> toggleStatus(
            @PathVariable Long id,
            @RequestParam Alert.AlertStatus status) {
        return ResponseEntity.ok(alertService.toggleAlertStatus(id, status));
    }
}
