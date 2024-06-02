package net.etfbl.pj2.parser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.etfbl.pj2.exception.ParsingException;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;

/**
 * Parses daily reports from text files.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class DailyReportParser {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy.");

	/**
	 * Parses daily reports from text files and returns a map of report names to
	 * DailyReport objects.
	 * 
	 * @return A map of report names to DailyReport objects.
	 */
	public Map<String, DailyReport> parseDailyReport() {
		Map<String, String> reports = ReportFileManager.loadReportFromTextFile(new DailyReport());
		Map<String, DailyReport> dailyReports = new HashMap<>();

		reports.forEach((name, text) -> {
			DailyReport report = parseText(text);
			dailyReports.put(name, report);
		});

		return dailyReports;
	}

	/**
	 * Parses the text of a daily report and extracts relevant information.
	 * 
	 * @param reportText The text that needs to be parsed
	 */
	private DailyReport parseText(String reportText) {
		DailyReport report = new DailyReport();
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
	 * Parses a BigDecimal value from the given line of text.
	 * 
	 * @param line The line of text containing the BigDecimal value.
	 * @return The parsed BigDecimal value.
	 */
	private BigDecimal parseBigDecimal(String line) {
		return new BigDecimal(line.split(":")[1].trim());
	}

}
