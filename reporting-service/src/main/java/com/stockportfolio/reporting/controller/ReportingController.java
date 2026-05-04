package com.stockportfolio.reporting.controller;

import com.stockportfolio.reporting.dto.PortfolioSummaryResponse;
import com.stockportfolio.reporting.service.ExcelExportService;
import com.stockportfolio.reporting.service.PdfExportService;
import com.stockportfolio.reporting.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reports", description = "Portfolio summary reports and PDF/Excel export")
@SecurityRequirement(name = "bearerAuth")
public class ReportingController {

    private final ReportingService reportingService;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    @Operation(
        summary = "Get portfolio summary",
        description = "Returns a complete portfolio summary including total invested value, current value, " +
                      "gain/loss, and breakdown by portfolio. Pass `X-User-Id` header."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Summary returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioSummaryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Missing X-User-Id header", content = @Content),
        @ApiResponse(responseCode = "503", description = "Portfolio service unavailable", content = @Content)
    })
    @GetMapping("/portfolio-summary")
    public ResponseEntity<PortfolioSummaryResponse> getPortfolioSummary(
            @Parameter(description = "User ID (injected by API Gateway from JWT)", example = "1")
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reportingService.getPortfolioSummary(userId));
    }

    @Operation(
        summary = "Export portfolio report",
        description = "Exports the portfolio report as a downloadable file.\n\n" +
                      "- `type=pdf` → Downloads `portfolio-report.pdf` (iText)\n" +
                      "- `type=excel` → Downloads `portfolio-report.xlsx` (Apache POI)\n\n" +
                      "Pass `X-User-Id` header with the user's ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File downloaded successfully",
            content = {
                @Content(mediaType = "application/pdf"),
                @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }),
        @ApiResponse(responseCode = "400", description = "Invalid type parameter — use 'pdf' or 'excel'", content = @Content),
        @ApiResponse(responseCode = "500", description = "Export generation failed", content = @Content)
    })
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @Parameter(description = "Export format", example = "pdf",
                schema = @Schema(allowableValues = {"pdf", "excel"}))
            @RequestParam String type,
            @Parameter(description = "User ID (injected by API Gateway from JWT)", example = "1")
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
