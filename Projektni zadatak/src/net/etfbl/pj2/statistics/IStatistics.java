package net.etfbl.pj2.statistics;

import java.math.BigDecimal;

/**
 * Interface for calculating various statistics related to rentals and expenses.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public interface IStatistics {
	/**
	 * Calculates the total income.
	 *
	 * @return The total income.
	 */
	public BigDecimal calculateTotalIncome();

	/**
	 * Calculates the total discount applied.
	 *
	 * @return The total discount.
	 */
	public BigDecimal calculateTotalDiscount();

	/**
	 * Calculates the total promotions applied.
	 *
	 * @return The total promotions.
	 */
	public BigDecimal calculateTotalPromotions();

	/**
	 * Calculates the total income generated in the wide area.
	 *
	 * @return The total income in the wide area.
	 */
	public BigDecimal calculateTotalIncomeInWideArea();

	/**
	 * Calculates the total income generated in the narrow area.
	 *
	 * @return The total income in the narrow area.
	 */
	public BigDecimal calculateTotalIncomeInNarrowArea();

	/**
	 * Calculates the total amount spent on maintenance.
	 *
	 * @return The total maintenance cost.
	 */
	public BigDecimal calculateTotalAmountForMaintenance();

	/**
	 * Calculates the total amount spent on maintenance based on total income.
	 *
	 * @param totalIncome The total income.
	 * @return The total maintenance cost.
	 */
	public BigDecimal calculateTotalAmountForMaintence(BigDecimal totalIncome);

	/**
	 * Calculates the total cost.
	 *
	 * @return The total cost.
	 */
	public BigDecimal calculateTotalCost();

	/**
	 * Calculates the total cost based on total income.
	 *
	 * @param totalIncome The total income.
	 * @return The total cost.
	 */
	public BigDecimal calculateTotalCost(BigDecimal totalIncome);

	/**
	 * Calculates the total tax.
	 *
	 * @return The total tax.
	 */
	public BigDecimal calculateTotalTax();

	/**
	 * Calculates the total tax based on total income, maintenance cost, repair
	 * cost, and total cost.
	 *
	 * @param totalIncome   The total income.
	 * @param maintenceCost The maintenance cost.
	 * @param repairCost    The repair cost.
	 * @param totalCost     The total cost.
	 * @return The total tax.
	 */
	public BigDecimal calculateTotalTax(BigDecimal totalIncome, BigDecimal maintenceCost, BigDecimal repairCost,
			BigDecimal totalCost);

	/**
	 * Calculates the total amount spent on repairs.
	 *
	 * @return The total repair cost.
	 */
	public BigDecimal calculateTotalAmountForRepairs();
}
