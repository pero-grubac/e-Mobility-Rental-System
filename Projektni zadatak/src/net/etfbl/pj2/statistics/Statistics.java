package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import net.etfbl.pj2.exception.UnknownVehicleException;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Class for calculating statistics related to rentals and expenses.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Statistics implements IStatistics {
	private List<Invoice> invoices;
	private AppConfig conf = new AppConfig();

	/**
	 * Constructs a Statistics object with the given list of invoices.
	 *
	 * @param invoices The list of invoices.
	 */
	public Statistics(List<Invoice> invoices) {
		super();
		this.invoices = invoices;
	}

	/**
	 * Calculates the total income from all invoices.
	 *
	 * @return The total income.
	 */
	@Override
	public BigDecimal calculateTotalIncome() {
		return invoices.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total discount applied to all invoices.
	 *
	 * @return The total discount amount.
	 */
	@Override
	public BigDecimal calculateTotalDiscount() {
		return BigDecimal.valueOf(invoices.stream().mapToDouble(Invoice::getDiscount).sum())
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total amount of promotions applied to all invoices.
	 *
	 * @return The total promotion amount.
	 */
	@Override
	public BigDecimal calculateTotalPromotions() {
		return BigDecimal.valueOf(invoices.stream().mapToDouble(Invoice::getPromotion).sum())
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total income earned in wide areas based on invoice data.
	 *
	 * @return The total income in wide areas.
	 */
	@Override
	public BigDecimal calculateTotalIncomeInWideArea() {
		Double temp = 0.0;
		AppConfig conf = new AppConfig();
		for (Invoice invoice : invoices)
			for (Field field : invoice.getRental().getShortestPath())
				if (field.getX() < conf.getNarrowBeginingXAxis() || field.getX() > conf.getNarrowEndXAxis()
						|| field.getY() < conf.getNarrowBeginingYAxis() || field.getY() > conf.getNarrowEndYAxis())
					temp += invoice.getBasicPrice() * conf.getDistanceWide();
		return BigDecimal.valueOf(temp).setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total income earned in narrow areas based on invoice data.
	 *
	 * @return The total income in narrow areas.
	 */
	@Override
	public BigDecimal calculateTotalIncomeInNarrowArea() {
		Double temp = 0.0;
		for (Invoice invoice : invoices)
			for (Field field : invoice.getRental().getShortestPath())
				if (field.getX() >= conf.getNarrowBeginingXAxis() && field.getX() <= conf.getNarrowEndXAxis()
						&& field.getY() >= conf.getNarrowBeginingYAxis() && field.getY() <= conf.getNarrowEndYAxis())
					temp += invoice.getBasicPrice() * conf.getDistanceNarrow();
		return BigDecimal.valueOf(temp).setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total amount spent on maintenance.
	 *
	 * @return The total maintenance cost.
	 */
	@Override
	public BigDecimal calculateTotalAmountForMaintenance() {
		return calculateTotalIncome().multiply(BigDecimal.valueOf(conf.getMaintenceCost()))
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total cost considering income, maintenance, and repair
	 * expenses.
	 *
	 * @return The total cost.
	 */
	@Override
	public BigDecimal calculateTotalCost() {
		return calculateTotalIncome().multiply(BigDecimal.valueOf(conf.getTotalCostPer()))
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total tax applied to the income after deducting maintenance,
	 * repair, and total cost.
	 *
	 * @return The total tax amount.
	 */
	@Override
	public BigDecimal calculateTotalTax() {
		BigDecimal temp = calculateTotalIncome().subtract(calculateTotalAmountForMaintenance())
				.subtract(calculateTotalAmountForRepairs()).subtract(calculateTotalCost());
		return temp.multiply(BigDecimal.valueOf(conf.getTax())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total amount spent on repairs.
	 *
	 * @return The total repair cost.
	 */
	@Override
	public BigDecimal calculateTotalAmountForRepairs() {
		Double temp = 0.0;
		TransportVehicle vehicele;
		try {
			for (Invoice invoice : invoices) {
				vehicele = invoice.getVehicle();
				if (vehicele instanceof Car)
					temp += vehicele.getPurchasePrice() * conf.getCarRepairPrice();
				else if (vehicele instanceof ElectricBike)
					temp += vehicele.getPurchasePrice() * conf.getBikeUnitPrice();
				else if (vehicele instanceof ElectricScooter)
					temp += vehicele.getPurchasePrice() * conf.getBikeUnitPrice();
				else
					throw new UnknownVehicleException(vehicele.getClass().getName());
			}
		} catch (UnknownVehicleException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		// baci ex
		return BigDecimal.valueOf(temp).setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total maintenance cost based on the total income.
	 *
	 * @param totalIncome The total income.
	 * @return The total maintenance cost.
	 */
	@Override
	public BigDecimal calculateTotalAmountForMaintence(BigDecimal totalIncome) {
		return totalIncome.multiply(BigDecimal.valueOf(conf.getMaintenceCost())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total cost based on the total income.
	 *
	 * @param totalIncome The total income.
	 * @return The total cost.
	 */
	@Override
	public BigDecimal calculateTotalCost(BigDecimal totalIncome) {
		return totalIncome.multiply(BigDecimal.valueOf(conf.getTotalCostPer())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total tax applied to the income after deducting maintenance,
	 * repair, and total cost.
	 *
	 * @param totalIncome   The total income.
	 * @param maintenceCost The total maintenance cost.
	 * @param repairCost    The total repair cost.
	 * @param totalCost     The total cost.
	 * @return The total tax amount.
	 */
	@Override
	public BigDecimal calculateTotalTax(BigDecimal totalIncome, BigDecimal maintenceCost, BigDecimal repairCost,
			BigDecimal totalCost) {
		BigDecimal temp = totalIncome.subtract(maintenceCost).subtract(repairCost).subtract(totalCost);
		return temp.multiply(BigDecimal.valueOf(conf.getTax())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

}
