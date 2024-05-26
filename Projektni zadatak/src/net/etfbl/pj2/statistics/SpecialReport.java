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

public class SpecialReport implements Serializable {
	private Map<TransportVehicle, BigDecimal> topVehiclesByIncome = new HashMap<>();

	public SpecialReport(List<Invoice> invoices) {
		findMostIncomeVehices(invoices);
	}

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

	private String generateText() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vehicles with the most revenue:").append("\n");
		topVehiclesByIncome.forEach((key, value) -> {
			builder.append("  Vehicle: ").append(key).append("\n");
			builder.append("    Revenue: ").append(value).append("\n");
		});
		return builder.toString();
	}

	@Override
	public String toString() {
		return generateText();
	}

}
