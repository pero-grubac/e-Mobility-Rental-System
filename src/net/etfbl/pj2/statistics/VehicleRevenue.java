package net.etfbl.pj2.statistics;

import java.io.Serializable;
import java.math.BigDecimal;

import net.etfbl.pj2.model.TransportVehicle;

/**
 * A vehicle with all its fields and the revenue it brought. One object of this
 * class is serialized to its own binary file by the special report.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class VehicleRevenue implements Serializable {
	private static final long serialVersionUID = 1L;

	private final TransportVehicle vehicle;
	private final BigDecimal revenue;
	private final int rentals;

	/**
	 * @param vehicle The vehicle.
	 * @param revenue The sum of the totals of its invoices.
	 * @param rentals The number of its rentals.
	 */
	public VehicleRevenue(TransportVehicle vehicle, BigDecimal revenue, int rentals) {
		this.vehicle = vehicle;
		this.revenue = revenue;
		this.rentals = rentals;
	}

	public TransportVehicle getVehicle() {
		return vehicle;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public int getRentals() {
		return rentals;
	}

	@Override
	public String toString() {
		return vehicle + ": " + revenue.toPlainString() + " from " + rentals + " rentals";
	}
}
