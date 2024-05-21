package net.etfbl.pj2.statistics;

import java.math.BigDecimal;

public interface Statistics {
	public BigDecimal totalIncome();

	public BigDecimal totalDiscount();

	public BigDecimal totalPromotions();

	public BigDecimal totalIncomeInWideArea();

	public BigDecimal totalIncomeInNarrowArea();

	public BigDecimal totalAmountForMaintence();

}
