package com.stockportfolio.reporting.controller;

import com.stockportfolio.reporting.dto.PortfolioSummaryResponse;
import com.stockportfolio.reporting.service.ExcelExportService;
import com.stockportfolio.reporting.service.PdfExportService;
import com.stockportfolio.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Reporting controller - portfolio summaries and export endpoints.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    /**
     * GET /api/reports/portfolio-summary
     * Get daily portfolio summary for the authenticated user.
     */
    @GetMapping("/portfolio-summary")
    public ResponseEntity<PortfolioSummaryResponse> getPortfolioSummary(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reportingService.getPortfolioSummary(userId));
    }

    /**
     * GET /api/reports/export?type=pdf|excel
     * Export portfolio report as PDF or Excel.
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam String type,
            @RequestHeader("X-User-Id") Long userId) {

        PortfolioSummaryResponse summary = reportingService.getPortfolioSummary(userId);

        try {
            if ("excel".equalsIgnoreCase(type)) {
                byte[] data = excelExportService.exportToExcel(summary);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=portfolio-report.xlsx")
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(data);

            } else if ("pdf".equalsIgnoreCase(type)) {
                byte[] data = pdfExportService.exportToPdf(summary);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=portfolio-report.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(data);

            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
