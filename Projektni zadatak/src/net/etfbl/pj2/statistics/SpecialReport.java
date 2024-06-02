package net.etfbl.pj2.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;

/**
 * Represents a special report containing information about vehicles with the
 * most revenue.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SpecialReport implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<TransportVehicle, BigDecimal> topVehiclesByIncome = new HashMap<>();

	/**
	 * Constructs a SpecialReport object based on the provided list of invoices.
	 *
	 * @param invoices The list of invoices to analyze.
	 */
	public SpecialReport(List<Invoice> invoices) {
		findMostIncomeVehices(invoices);
	}

	/**
	 * Finds the vehicles with the highest revenue in the provided list of invoices.
	 *
	 * @param invoices The list of invoices to analyze.
	 */
	private void findMostIncomeVehices(List<Invoice> invoices) {
		TransportVehicle car = null;
		BigDecimal carIncome = BigDecimal.ZERO;
		TransportVehicle bike = null;
		BigDecimal bikeIncome = BigDecimal.ZERO;
		TransportVehicle scooter = null;
		BigDecimal scooterIncome = BigDecimal.ZERO;

		for (Invoice invoice : invoices) {
			if (invoice.getVehicle() instanceof Car && invoice.getTotalAmount().compareTo(carIncome) > 0) {
				car = invoice.getVehicle();
				carIncome = invoice.getTotalAmount();
			} else if (invoice.getVehicle() instanceof ElectricBike
					&& invoice.getTotalAmount().compareTo(bikeIncome) > 0) {
				bike = invoice.getVehicle();
				bikeIncome = invoice.getTotalAmount();
			} else if (invoice.getVehicle() instanceof ElectricScooter
					&& invoice.getTotalAmount().compareTo(scooterIncome) > 0) {
				scooter = invoice.getVehicle();
				scooterIncome = invoice.getTotalAmount();
			}
		}
		topVehiclesByIncome.put(car, carIncome);
		topVehiclesByIncome.put(bike, bikeIncome);
		topVehiclesByIncome.put(scooter, scooterIncome);

	}

	/**
	 * Generates the text representation of the special report.
	 *
	 * @return The textual representation of the special report.
	 */
	private String generateText() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vehicles with the most revenue:").append("\n");
		topVehiclesByIncome.forEach((key, value) -> {
			builder.append("  Vehicle: ").append(key).append("\n");
			builder.append("    Revenue: ").append(value).append("\n");
		});
		return builder.toString();
	}

	public Map<TransportVehicle, BigDecimal> getTopVehiclesByIncome() {
		return topVehiclesByIncome;
	}

	public void setTopVehiclesByIncome(Map<TransportVehicle, BigDecimal> topVehiclesByIncome) {
		this.topVehiclesByIncome = topVehiclesByIncome;
	}

	@Override
	public String toString() {
		return generateText();
	}

}
