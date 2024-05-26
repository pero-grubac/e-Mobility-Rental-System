package net.etfbl.pj2.statistics;

import java.math.BigDecimal;

public interface IStatistics {
	public BigDecimal calculateTotalIncome();

	public BigDecimal calculateTotalDiscount();

	public BigDecimal calculateTotalPromotions();

	public BigDecimal calculateTotalIncomeInWideArea();

	public BigDecimal calculateTotalIncomeInNarrowArea();

	public BigDecimal calculateTotalAmountForMaintence();

	public BigDecimal calculateTotalAmountForMaintence(BigDecimal totalIncome);

	public BigDecimal calculateTotalCost();

	public BigDecimal calculateTotalCost(BigDecimal totalIncome);

	public BigDecimal calculateTotalTax();

	public BigDecimal calculateTotalTax(BigDecimal totalIncome,BigDecimal maintenceCost,BigDecimal repairCost,BigDecimal totalCost);

	public BigDecimal calculateTotalAmountForRepairs();
}
