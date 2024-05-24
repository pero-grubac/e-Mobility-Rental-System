package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

public class Statistics implements IStatistics {
	private List<Invoice> invoices;
	private AppConfig conf = new AppConfig();

	public Statistics(List<Invoice> invoices) {
		super();
		this.invoices = invoices;
	}

	@Override
	public BigDecimal calculateTotalIncome() {
		return invoices.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalDiscount() {
		return BigDecimal.valueOf(invoices.stream().mapToDouble(Invoice::getDiscount).sum())
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalPromotions() {
		return BigDecimal.valueOf(invoices.stream().mapToDouble(Invoice::getPromotion).sum())
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalIncomeInWideArea() {
		Double temp = 0.0;
		AppConfig conf = new AppConfig();
		for (Invoice invoice : invoices)
			for (Field field : invoice.getRental().getShortestPath())
				if (field.getX() <= 10 || field.getY() <= 10)
					temp += invoice.getBasicPrice() * conf.getDistanceWide();
		return BigDecimal.valueOf(temp).setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalIncomeInNarrowArea() {
		Double temp = 0.0;
		for (Invoice invoice : invoices)
			for (Field field : invoice.getRental().getShortestPath())
				if (field.getX() > 10 || field.getY() > 10)
					temp += invoice.getBasicPrice() * conf.getDistanceNarrow();
		return BigDecimal.valueOf(temp).setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalAmountForMaintence() {
		return calculateTotalIncome().multiply(BigDecimal.valueOf(conf.getMaintenceCost()))
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalCost() {
		return calculateTotalIncome().multiply(BigDecimal.valueOf(conf.getTotalCostPer()))
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalTax() {
		BigDecimal temp = calculateTotalIncome().subtract(calculateTotalAmountForMaintence())
				.subtract(calculateTotalAmountForRepairs());
		return temp.multiply(BigDecimal.valueOf(conf.getTax())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalAmountForRepairs() {
		Double temp = 0.0;
		TransportVehicle vehicele;
		for (Invoice invoice : invoices) {
			vehicele = invoice.getVehicle();
			if (vehicele instanceof Car)
				temp += vehicele.getPurchasePrice() * conf.getCarRepairPrice();
			else if (vehicele instanceof ElectricBike)
				temp += vehicele.getPurchasePrice() * conf.getBikeUnitPrice();
			else if (vehicele instanceof ElectricScooter)
				temp += vehicele.getPurchasePrice() * conf.getBikeUnitPrice();
			else
				System.out.println("nesto je poslo po zlu");
		}
		// baci ex
		return BigDecimal.valueOf(temp).setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalAmountForMaintence(BigDecimal totalIncome) {
		return totalIncome.multiply(BigDecimal.valueOf(conf.getMaintenceCost())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalCost(BigDecimal totalIncome) {
		return totalIncome.multiply(BigDecimal.valueOf(conf.getTotalCostPer())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalTax(BigDecimal totalIncome, BigDecimal maintenceCost, BigDecimal repairCost) {
		BigDecimal temp = totalIncome.subtract(maintenceCost).subtract(repairCost);
		return temp.multiply(BigDecimal.valueOf(conf.getTax())).setScale(conf.getBigDecimalRound(),
				RoundingMode.HALF_UP);
	}

}
