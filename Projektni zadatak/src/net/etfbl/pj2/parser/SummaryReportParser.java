package net.etfbl.pj2.parser;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.etfbl.pj2.exception.DuplicateException;
import net.etfbl.pj2.exception.ParsingException;
import net.etfbl.pj2.exception.UnknownVehicleException;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.statistics.SummaryReport;

/**
 * Parses summary reports.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SummaryReportParser {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy.");

	/**
	 * Parses daily summary reports.
	 * 
	 * @return A map containing parsed summary reports.
	 */
	public Map<String, SummaryReport> parseDailyReport() {
		Map<String, String> reports = ReportFileManager.loadReportFromTextFile(new SummaryReport());
		Map<String, SummaryReport> dailyReports = new HashMap<>();

		reports.forEach((name, text) -> {
			SummaryReport report = parseText(text);
			dailyReports.put(name, report);
		});

		return dailyReports;
	}

	/**
	 * Parses the text of a summary report.
	 * 
	 * @param reportText The text of the report to parse.
	 * @return A parsed summary report.
	 */
	private SummaryReport parseText(String reportText) {
		SummaryReport report = new SummaryReport();
		List<BigDecimal> incomes = new ArrayList<>();
		VehicleParser vehicleParser = new VehicleParser();
		String[] lines = reportText.split("\n");

		for (String line : lines) {
			try {
				if (line.startsWith("Total Income:")) {
					report.setTotalIncome(parseBigDecimal(line));
				} else if (line.startsWith("Total Discount:")) {
					report.setTotalDiscount(parseBigDecimal(line));
				} else if (line.startsWith("Total Promotions:")) {
					report.setTotalPromotions(parseBigDecimal(line));
				} else if (line.startsWith("Total Income in Wide Area:")) {
					report.setTotalIncomeInWideArea(parseBigDecimal(line));
				} else if (line.startsWith("Total Income in Narrow Area:")) {
					report.setTotalIncomeInNarrowArea(parseBigDecimal(line));
				} else if (line.startsWith("Total Amount for Maintenance:")) {
					report.setTotalAmountForMaintenance(parseBigDecimal(line));
				} else if (line.startsWith("Total Amount for Repairs:")) {
					report.setTotalAmountForRepairs(parseBigDecimal(line));
				} else if (line.startsWith("Total Tax::")) {
					report.setTotalTax(parseBigDecimal(line));
				} else if (line.startsWith("Total cost:")) {
					report.setTotalCost(parseBigDecimal(line));
				} else if (line.startsWith("   ")) {
					incomes.add(BigDecimal.valueOf(Double.parseDouble(line.trim())));
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
		report.setIncomes(incomes);
		return report;
	}

	/**
	 * Parses a BigDecimal value from a line of text.
	 * 
	 * @param line The line of text containing the BigDecimal value.
	 * @return The parsed BigDecimal value.
	 */
	private BigDecimal parseBigDecimal(String line) {
		return new BigDecimal(line.split(":")[1].trim());
	}
}
