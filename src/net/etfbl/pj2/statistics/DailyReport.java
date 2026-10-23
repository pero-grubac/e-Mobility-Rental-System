package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Report of one day: total income, discounts, promotions, income in the narrow
 * and the wide part of the city, maintenance and repairs.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class DailyReport {
	private final LocalDate date;
	private final int rentals;
	private final BigDecimal totalIncome;
	private final BigDecimal totalDiscount;
	private final BigDecimal totalPromotions;
	private final BigDecimal totalIncomeInNarrowArea;
	private final BigDecimal totalIncomeInWideArea;
	private final BigDecimal totalAmountForMaintenance;
	private final BigDecimal totalAmountForRepairs;

	/**
	 * Calculates the report of the given invoices.
	 *
	 * @param date     The date of the report, or null for a report of all dates.
	 * @param invoices The invoices.
	 * @param conf     The application configuration.
	 */
	public DailyReport(LocalDate date, List<Invoice> invoices, AppConfig conf) {
		this(date, invoices.size(), new Statistics(invoices, conf));
	}

	/**
	 * Builds the report from already prepared statistics.
	 *
	 * @param date       The date of the report, or null.
	 * @param rentals    The number of rentals.
	 * @param statistics The statistics.
	 */
	protected DailyReport(LocalDate date, int rentals, IStatistics statistics) {
		this.date = date;
		this.rentals = rentals;
		this.totalIncome = statistics.calculateTotalIncome();
		this.totalDiscount = statistics.calculateTotalDiscount();
		this.totalPromotions = statistics.calculateTotalPromotions();
		this.totalIncomeInNarrowArea = statistics.calculateTotalIncomeInNarrowArea();
		this.totalIncomeInWideArea = statistics.calculateTotalIncomeInWideArea();
		this.totalAmountForMaintenance = statistics.calculateTotalAmountForMaintenance();
		this.totalAmountForRepairs = statistics.calculateTotalAmountForRepairs();
	}

	/**
	 * @return The report as text, one item per line.
	 */
	public String toText() {
		StringBuilder text = new StringBuilder();
		line(text, "Rentals", String.valueOf(rentals));
		line(text, "Total income", totalIncome);
		line(text, "Total discount", totalDiscount);
		line(text, "Total promotions", totalPromotions);
		line(text, "Income in narrow area", totalIncomeInNarrowArea);
		line(text, "Income in wide area", totalIncomeInWideArea);
		line(text, "Maintenance", totalAmountForMaintenance);
		line(text, "Repairs", totalAmountForRepairs);
		return text.toString();
	}

	/**
	 * Appends one aligned "label: value" line.
	 *
	 * @param text  The text to append to.
	 * @param label The label.
	 * @param value The value.
	 */
	protected static void line(StringBuilder text, String label, Object value) {
		String shown = value instanceof BigDecimal ? ((BigDecimal) value).toPlainString() : String.valueOf(value);
		text.append(String.format(Locale.ROOT, "%-24s%14s%n", label + ":", shown));
	}

	public LocalDate getDate() {
		return date;
	}

	public int getRentals() {
		return rentals;
	}

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	public BigDecimal getTotalPromotions() {
		return totalPromotions;
	}

	public BigDecimal getTotalIncomeInNarrowArea() {
		return totalIncomeInNarrowArea;
	}

	public BigDecimal getTotalIncomeInWideArea() {
		return totalIncomeInWideArea;
	}

	public BigDecimal getTotalAmountForMaintenance() {
		return totalAmountForMaintenance;
	}

	public BigDecimal getTotalAmountForRepairs() {
		return totalAmountForRepairs;
	}

	@Override
	public String toString() {
		return toText();
	}
}
