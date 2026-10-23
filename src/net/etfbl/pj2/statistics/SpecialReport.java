package net.etfbl.pj2.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;

/**
 * Additional feature 1 of the task: for every vehicle type, the vehicle that
 * brought the most revenue. The revenue of a vehicle is the sum of the totals
 * of all its invoices.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SpecialReport {
	private final List<VehicleRevenue> topVehicles;

	/**
	 * Finds the top vehicle of every type in the given invoices.
	 *
	 * @param invoices The invoices.
	 */
	public SpecialReport(List<Invoice> invoices) {
		Map<TransportVehicle, List<Invoice>> byVehicle = new LinkedHashMap<>();
		invoices.forEach(i -> byVehicle.computeIfAbsent(i.getVehicle(), v -> new ArrayList<>()).add(i));

		List<VehicleRevenue> revenues = byVehicle.entrySet().stream()
				.map(e -> new VehicleRevenue(e.getKey(),
						e.getValue().stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
						e.getValue().size()))
				.toList();

		List<VehicleRevenue> top = new ArrayList<>();
		for (VehicleType type : VehicleType.values()) {
			Optional<VehicleRevenue> best = revenues.stream().filter(r -> r.getVehicle().getType() == type)
					.max(Comparator.comparing(VehicleRevenue::getRevenue));
			best.ifPresent(top::add);
		}
		this.topVehicles = List.copyOf(top);
	}

	/**
	 * @return The top vehicle of every type that has invoices, in the order car,
	 *         bike, scooter.
	 */
	public List<VehicleRevenue> getTopVehicles() {
		return topVehicles;
	}
}
