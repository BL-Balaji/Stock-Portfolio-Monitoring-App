package com.stockportfolio.reporting.service;

import com.stockportfolio.reporting.dto.HoldingData;
import com.stockportfolio.reporting.dto.PortfolioData;
import com.stockportfolio.reporting.dto.PortfolioSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service to export portfolio data to Excel format using Apache POI.
 */
@Service
@Slf4j
public class ExcelExportService {

    public byte[] exportToExcel(PortfolioSummaryResponse summary) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Summary sheet
            createSummarySheet(workbook, summary);

            // Holdings sheet
            createHoldingsSheet(workbook, summary.getPortfolios());

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createSummarySheet(Workbook workbook, PortfolioSummaryResponse summary) {
        Sheet sheet = workbook.createSheet("Portfolio Summary");
        CellStyle headerStyle = createHeaderStyle(workbook);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Stock Portfolio Summary Report");
        titleCell.setCellStyle(headerStyle);

        rowNum++; // blank row

        String[][] summaryData = {
                {"Total Portfolios", String.valueOf(summary.getTotalPortfolios())},
                {"Total Holdings", String.valueOf(summary.getTotalHoldings())},
                {"Total Invested Value", "$" + summary.getTotalInvestedValue()},
                {"Total Current Value", "$" + summary.getTotalCurrentValue()},
                {"Total Gain/Loss", "$" + summary.getTotalGainLoss()},
                {"Total Gain/Loss %", summary.getTotalGainLossPercentage() + "%"},
                {"Generated At", summary.getGeneratedAt().toString()}
        };

        for (String[] data : summaryData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data[0]);
            row.createCell(1).setCellValue(data[1]);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createHoldingsSheet(Workbook workbook, List<PortfolioData> portfolios) {
        Sheet sheet = workbook.createSheet("Holdings");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {"Portfolio", "Symbol", "Name", "Quantity",
                "Buy Price", "Current Price", "Invested Value",
                "Current Value", "Gain/Loss", "Gain/Loss %"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (PortfolioData portfolio : portfolios) {
            if (portfolio.getHoldings() == null) continue;
            for (HoldingData holding : portfolio.getHoldings()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(portfolio.getName());
                row.createCell(1).setCellValue(holding.getStockSymbol());
                row.createCell(2).setCellValue(holding.getStockName() != null ? holding.getStockName() : "");
                row.createCell(3).setCellValue(holding.getQuantity());
                row.createCell(4).setCellValue(holding.getBuyPrice().doubleValue());
                row.createCell(5).setCellValue(holding.getCurrentPrice().doubleValue());
                row.createCell(6).setCellValue(holding.getInvestedValue().doubleValue());
                row.createCell(7).setCellValue(holding.getCurrentValue().doubleValue());
                row.createCell(8).setCellValue(holding.getGainLoss().doubleValue());
                row.createCell(9).setCellValue(holding.getGainLossPercentage().doubleValue());
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
