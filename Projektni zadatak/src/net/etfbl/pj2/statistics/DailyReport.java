package net.etfbl.pj2.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;

/**
 * Represents a daily report summarizing rental statistics for a specific date.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class DailyReport {

	protected transient Statistics statistcs;
	protected BigDecimal totalIncome;
	protected BigDecimal totalDiscount;
	protected BigDecimal totalPromotions;
	protected BigDecimal totalIncomeInWideArea;
	protected BigDecimal totalIncomeInNarrowArea;
	protected BigDecimal totalAmountForMaintenance;
	protected BigDecimal totalAmountForRepairs;
	protected List<BigDecimal> incomes = new ArrayList<BigDecimal>();
	protected LocalDate date;

	/**
	 * Default constructor.
	 */
	public DailyReport() {
	}

	/**
	 * Constructs a daily report based on the provided list of invoices.
	 *
	 * @param invoices List of invoices for the day.
	 */
	public DailyReport(List<Invoice> invoices) {
		statistcs = new Statistics(invoices);

		invoices.stream().forEach(i -> incomes.add(i.getTotalAmount()));
		totalIncome = statistcs.calculateTotalIncome();
		totalDiscount = statistcs.calculateTotalDiscount();
		totalPromotions = statistcs.calculateTotalPromotions();
		totalIncomeInNarrowArea = statistcs.calculateTotalIncomeInNarrowArea();
		totalIncomeInWideArea = statistcs.calculateTotalIncomeInWideArea();
		totalAmountForMaintenance = statistcs.calculateTotalAmountForMaintence(totalIncome);
		totalAmountForRepairs = statistcs.calculateTotalAmountForRepairs();
	}

	/**
	 * Generates the text content of the daily report.
	 *
	 * @return The text content of the report.
	 */
	protected String generateReportText() {
		StringBuilder reportContent = new StringBuilder();
		reportContent.append("Total Income: ").append(totalIncome).append("\n");
		reportContent.append("Total Discount: ").append(totalDiscount).append("\n");
		reportContent.append("Total Promotions: ").append(totalPromotions).append("\n");
		reportContent.append("Total Income in Wide Area: ").append(totalIncomeInWideArea).append("\n");
		reportContent.append("Total Income in Narrow Area: ").append(totalIncomeInNarrowArea).append("\n");
		reportContent.append("Total Amount for Maintenance: ").append(totalAmountForMaintenance).append("\n");
		reportContent.append("Total Amount for Repairs: ").append(totalAmountForRepairs).append("\n");

		return reportContent.toString();
	}

	/**
	 * Generates the complete report including vehicle incomes.
	 *
	 * @return The complete report with vehicle incomes.
	 */
	protected String reportWithVehicles() {
		StringBuilder reportContent = new StringBuilder();
		reportContent.append(generateReportText());
		reportContent.append("List of incomes: ").append("\n");

		for (BigDecimal income : incomes) {
			reportContent.append("   ").append(income).append("\n");
		}
		return reportContent.toString();
	}

	public Statistics getStatistcs() {
		return statistcs;
	}

	public void setStatistcs(Statistics statistcs) {
		this.statistcs = statistcs;
	}

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(BigDecimal totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public BigDecimal getTotalPromotions() {
		return totalPromotions;
	}

	public void setTotalPromotions(BigDecimal totalPromotions) {
		this.totalPromotions = totalPromotions;
	}

	public BigDecimal getTotalIncomeInWideArea() {
		return totalIncomeInWideArea;
	}

	public void setTotalIncomeInWideArea(BigDecimal totalIncomeInWideArea) {
		this.totalIncomeInWideArea = totalIncomeInWideArea;
	}

	public BigDecimal getTotalIncomeInNarrowArea() {
		return totalIncomeInNarrowArea;
	}

	public void setTotalIncomeInNarrowArea(BigDecimal totalIncomeInNarrowArea) {
		this.totalIncomeInNarrowArea = totalIncomeInNarrowArea;
	}

	public BigDecimal getTotalAmountForMaintenance() {
		return totalAmountForMaintenance;
	}

	public void setTotalAmountForMaintenance(BigDecimal totalAmountForMaintence) {
		this.totalAmountForMaintenance = totalAmountForMaintence;
	}

	public BigDecimal getTotalAmountForRepairs() {
		return totalAmountForRepairs;
	}

	public void setTotalAmountForRepairs(BigDecimal totalAmountForRepairs) {
		this.totalAmountForRepairs = totalAmountForRepairs;
	}

	public List<BigDecimal> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<BigDecimal> incomes) {
		this.incomes = incomes;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return reportWithVehicles();
	}

}
