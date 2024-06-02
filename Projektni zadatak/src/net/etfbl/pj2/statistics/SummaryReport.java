package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.util.List;

import net.etfbl.pj2.invoice.Invoice;

/**
 * Represents a summary report containing aggregated information from daily
 * reports.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SummaryReport extends DailyReport {
	private BigDecimal totalCost;
	private BigDecimal totalTax;

	/**
	 * Default constructor.
	 */
	public SummaryReport() {
	}

	/**
	 * Constructs a SummaryReport object based on the provided list of invoices.
	 *
	 * @param invoices The list of invoices to generate the summary report from.
	 */
	public SummaryReport(List<Invoice> invoices) {
		super(invoices);
		this.totalCost = statistcs.calculateTotalCost(totalIncome);
		this.totalTax = statistcs.calculateTotalTax(totalIncome, totalAmountForMaintenance, totalAmountForRepairs,
				totalCost);
	}

	/**
	 * Generates the text content of the summary report.
	 *
	 * @return The text content of the summary report.
	 */
	@Override
	protected String generateReportText() {
		StringBuilder reportContent = new StringBuilder(super.generateReportText());
		reportContent.append("Total Tax: ").append(totalTax).append("\n");
		reportContent.append("Total cost: ").append(totalCost).append("\n");
		return reportContent.toString();
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}

	@Override
	public String toString() {
		return reportWithVehicles();
	}

}
