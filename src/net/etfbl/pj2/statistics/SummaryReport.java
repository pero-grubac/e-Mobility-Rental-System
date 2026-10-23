package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.util.List;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Summary of all invoices: every item of the daily report plus the company
 * costs and the tax.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SummaryReport extends DailyReport {
	private final BigDecimal totalCost;
	private final BigDecimal totalTax;

	/**
	 * Calculates the summary of the given invoices.
	 *
	 * @param invoices The invoices.
	 * @param conf     The application configuration.
	 */
	public SummaryReport(List<Invoice> invoices, AppConfig conf) {
		this(invoices.size(), new Statistics(invoices, conf));
	}

	private SummaryReport(int rentals, Statistics statistics) {
		super(null, rentals, statistics);
		this.totalCost = statistics.calculateTotalCost();
		this.totalTax = statistics.calculateTotalTax();
	}

	@Override
	public String toText() {
		StringBuilder text = new StringBuilder(super.toText());
		line(text, "Company costs", totalCost);
		line(text, "Tax", totalTax);
		return text.toString();
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}
}
