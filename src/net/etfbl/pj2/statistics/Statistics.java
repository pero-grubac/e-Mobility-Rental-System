package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Calculates the business results of a list of invoices. All amounts are
 * rounded to the money scale of the configuration.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Statistics implements IStatistics {
	private final List<Invoice> invoices;
	private final AppConfig conf;

	/**
	 * @param invoices The invoices to analyze.
	 * @param conf     The application configuration.
	 */
	public Statistics(List<Invoice> invoices, AppConfig conf) {
		this.invoices = List.copyOf(invoices);
		this.conf = conf;
	}

	private BigDecimal sum(Predicate<Invoice> filter, Function<Invoice, BigDecimal> value) {
		return round(invoices.stream().filter(filter).map(value).reduce(BigDecimal.ZERO, BigDecimal::add));
	}

	private BigDecimal round(BigDecimal value) {
		return value.setScale(conf.getMoneyScale(), RoundingMode.HALF_UP);
	}

	private BigDecimal shareOfIncome(double rate) {
		return round(calculateTotalIncome().multiply(BigDecimal.valueOf(rate)));
	}

	@Override
	public BigDecimal calculateTotalIncome() {
		return sum(i -> true, Invoice::getTotalAmount);
	}

	@Override
	public BigDecimal calculateTotalDiscount() {
		return sum(i -> true, Invoice::getDiscount);
	}

	@Override
	public BigDecimal calculateTotalPromotions() {
		return sum(i -> true, Invoice::getPromotion);
	}

	@Override
	public BigDecimal calculateTotalIncomeInNarrowArea() {
		return sum(i -> !i.isWideArea(), Invoice::getAmount);
	}

	@Override
	public BigDecimal calculateTotalIncomeInWideArea() {
		return sum(Invoice::isWideArea, Invoice::getAmount);
	}

	@Override
	public BigDecimal calculateTotalAmountForMaintenance() {
		return shareOfIncome(conf.getMaintenanceRate());
	}

	@Override
	public BigDecimal calculateTotalAmountForRepairs() {
		return sum(i -> i.getRental().isBreakdown(), this::repairCost);
	}

	/**
	 * @param invoice An invoice of a ride with a breakdown.
	 * @return The repair coefficient of the vehicle type * its purchase price.
	 */
	public BigDecimal repairCost(Invoice invoice) {
		return BigDecimal.valueOf(conf.getRepairCoefficient(invoice.getVehicle().getType()))
				.multiply(BigDecimal.valueOf(invoice.getVehicle().getPurchasePrice()));
	}

	@Override
	public BigDecimal calculateTotalCost() {
		return shareOfIncome(conf.getCompanyCostRate());
	}

	@Override
	public BigDecimal calculateTotalTax() {
		BigDecimal base = calculateTotalIncome().subtract(calculateTotalAmountForMaintenance())
				.subtract(calculateTotalAmountForRepairs()).subtract(calculateTotalCost());
		return round(base.multiply(BigDecimal.valueOf(conf.getTaxRate())));
	}
}
