package net.etfbl.pj2.statistics;

import java.math.BigDecimal;

/**
 * Business results calculated from invoices.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public interface IStatistics {
	/**
	 * @return The sum of the amounts to pay on all invoices.
	 */
	BigDecimal calculateTotalIncome();

	/**
	 * @return The sum of all discounts.
	 */
	BigDecimal calculateTotalDiscount();

	/**
	 * @return The sum of all promotions.
	 */
	BigDecimal calculateTotalPromotions();

	/**
	 * @return The sum of the amounts of rides that stayed in the narrow part of
	 *         the city.
	 */
	BigDecimal calculateTotalIncomeInNarrowArea();

	/**
	 * @return The sum of the amounts of rides that used the wide part of the city.
	 */
	BigDecimal calculateTotalIncomeInWideArea();

	/**
	 * @return Maintenance: total income * the maintenance rate.
	 */
	BigDecimal calculateTotalAmountForMaintenance();

	/**
	 * @return The sum of the repair costs of all breakdowns.
	 */
	BigDecimal calculateTotalAmountForRepairs();

	/**
	 * @return Company costs: total income * the company cost rate.
	 */
	BigDecimal calculateTotalCost();

	/**
	 * @return Tax: (income - maintenance - repairs - company costs) * the tax
	 *         rate.
	 */
	BigDecimal calculateTotalTax();
}
