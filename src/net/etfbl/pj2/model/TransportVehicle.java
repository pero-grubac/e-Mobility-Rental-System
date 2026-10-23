package net.etfbl.pj2.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import net.etfbl.pj2.breakdown.Breakdown;
import net.etfbl.pj2.resources.AppConfig;

/**
 * A vehicle the company rents out. Every vehicle has a battery that is drained
 * while it moves and can be charged, and it can break down.
 * <p>
 * The battery level and the list of breakdowns are changed by the rental
 * threads and read by the GUI, so both are safe to share between threads.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public abstract class TransportVehicle implements Chargeable, Serializable {
	private static final long serialVersionUID = 2L;
	private static final AppConfig CONF = new AppConfig();

	private final String id;
	private final String manufacturer;
	private final String model;
	private final double purchasePrice;
	private volatile double batteryLevel;
	private final List<Breakdown> breakdowns = new CopyOnWriteArrayList<>();

	/**
	 * Constructs a new vehicle with a fully charged battery.
	 *
	 * @param id            The unique ID of the vehicle.
	 * @param manufacturer  The manufacturer.
	 * @param model         The model.
	 * @param purchasePrice The purchase price.
	 */
	protected TransportVehicle(String id, String manufacturer, String model, double purchasePrice) {
		this.id = id;
		this.manufacturer = manufacturer;
		this.model = model;
		this.purchasePrice = purchasePrice;
		chargeBattery();
	}

	/**
	 * @return The type of the vehicle, which selects its prices and rates in the
	 *         configuration.
	 */
	public abstract VehicleType getType();

	/**
	 * @return True if the vehicle can carry more than one person.
	 */
	public boolean canCarryMultiplePeople() {
		return false;
	}

	/**
	 * Charges the battery to its maximum level.
	 */
	@Override
	public void chargeBattery() {
		batteryLevel = CONF.getBatteryMaxLevel();
	}

	/**
	 * Drains the battery by the amount used for one field of the map. A battery
	 * that would run empty is charged first.
	 */
	@Override
	public synchronized void drainBattery() {
		double drain = CONF.getBatteryDrain(getType());
		if (batteryLevel - drain <= CONF.getBatteryMinLevel()) {
			chargeBattery();
		}
		batteryLevel -= drain;
	}

	/**
	 * Records a breakdown of this vehicle.
	 *
	 * @param dateTime When the breakdown happened.
	 * @param reason   Why the vehicle broke down.
	 * @return The recorded breakdown.
	 */
	public Breakdown breakDown(LocalDateTime dateTime, String reason) {
		Breakdown breakdown = new Breakdown(dateTime, reason);
		breakdowns.add(breakdown);
		return breakdown;
	}

	/**
	 * @return A copy of all breakdowns of this vehicle, oldest first.
	 */
	public List<Breakdown> getBreakdowns() {
		return new ArrayList<>(breakdowns);
	}

	public String getId() {
		return id;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	/**
	 * @return The battery level as text, for example "68%".
	 */
	public String getBatteryText() {
		return String.format(Locale.ROOT, "%.0f%%", batteryLevel);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return Objects.equals(id, ((TransportVehicle) o).id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return getType() + " " + id + " (" + manufacturer + " " + model + ")";
	}
}
