package com.stockportfolio.reporting.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stockportfolio.reporting.dto.HoldingData;
import com.stockportfolio.reporting.dto.PortfolioData;
import com.stockportfolio.reporting.dto.PortfolioSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * Service to export portfolio data to PDF format using iText.
 */
@Service
@Slf4j
public class PdfExportService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);

    public byte[] exportToPdf(PortfolioSummaryResponse summary) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Paragraph title = new Paragraph("Stock Portfolio Summary Report", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Summary section
        document.add(new Paragraph("Summary", SECTION_FONT));
        document.add(new Paragraph(" "));

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);
        summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        addSummaryRow(summaryTable, "Total Portfolios", String.valueOf(summary.getTotalPortfolios()));
        addSummaryRow(summaryTable, "Total Holdings", String.valueOf(summary.getTotalHoldings()));
        addSummaryRow(summaryTable, "Total Invested", "$" + summary.getTotalInvestedValue());
        addSummaryRow(summaryTable, "Total Current Value", "$" + summary.getTotalCurrentValue());
        addSummaryRow(summaryTable, "Total Gain/Loss", "$" + summary.getTotalGainLoss());
        addSummaryRow(summaryTable, "Total Gain/Loss %", summary.getTotalGainLossPercentage() + "%");
        addSummaryRow(summaryTable, "Generated At", summary.getGeneratedAt().toString());

        document.add(summaryTable);
        document.add(new Paragraph(" "));

        // Holdings section
        document.add(new Paragraph("Holdings Detail", SECTION_FONT));
        document.add(new Paragraph(" "));

        PdfPTable holdingsTable = new PdfPTable(9);
        holdingsTable.setWidthPercentage(100);

        String[] headers = {"Portfolio", "Symbol", "Qty", "Buy Price",
                "Current Price", "Invested", "Current Value", "Gain/Loss", "G/L %"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(new BaseColor(70, 130, 180));
            cell.setPadding(5);
            holdingsTable.addCell(cell);
        }

        for (PortfolioData portfolio : summary.getPortfolios()) {
            if (portfolio.getHoldings() == null) continue;
            for (HoldingData holding : portfolio.getHoldings()) {
                holdingsTable.addCell(new Phrase(portfolio.getName(), BODY_FONT));
                holdingsTable.addCell(new Phrase(holding.getStockSymbol(), BODY_FONT));
                holdingsTable.addCell(new Phrase(String.valueOf(holding.getQuantity()), BODY_FONT));
                holdingsTable.addCell(new Phrase("$" + holding.getBuyPrice(), BODY_FONT));
                holdingsTable.addCell(new Phrase("$" + holding.getCurrentPrice(), BODY_FONT));
                holdingsTable.addCell(new Phrase("$" + holding.getInvestedValue(), BODY_FONT));
                holdingsTable.addCell(new Phrase("$" + holding.getCurrentValue(), BODY_FONT));
                holdingsTable.addCell(new Phrase("$" + holding.getGainLoss(), BODY_FONT));
                holdingsTable.addCell(new Phrase(holding.getGainLossPercentage() + "%", BODY_FONT));
            }
        }

        document.add(holdingsTable);
        document.close();

        return out.toByteArray();
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BODY_FONT));
        labelCell.setPadding(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BODY_FONT));
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }
}
