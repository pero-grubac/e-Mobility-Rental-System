package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.util.List;

import net.etfbl.pj2.invoice.Invoice;

public class SummaryReport extends DailyReport {
	private BigDecimal totalCost;
	private BigDecimal totalTax;

	public SummaryReport() {
	}

	public SummaryReport(List<Invoice> invoices) {
		super(invoices);
		this.totalCost = statistcs.calculateTotalCost(totalIncome);
		this.totalTax = statistcs.calculateTotalTax(totalIncome, totalAmountForMaintence, totalAmountForRepairs,
				totalCost);
	}

	@Override
	protected String generateReportText() {
		StringBuilder reportContent = new StringBuilder(super.generateReportText());
		reportContent.append("Total Tax: ").append(totalTax).append("\n");
		reportContent.append("Total cost: ").append(totalCost).append("\n");
		return reportContent.toString();
	}

	@Override
	public String toString() {
		return reportWithVehicles();
	}

}
