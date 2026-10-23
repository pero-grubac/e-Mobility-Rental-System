package net.etfbl.pj2.model;

/**
 * An electric scooter, with its maximum speed.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class ElectricScooter extends TransportVehicle {
	private static final long serialVersionUID = 2L;

	private final int maxSpeed;

	/**
	 * Constructs a new electric scooter with a fully charged battery.
	 *
	 * @param id            The unique ID of the scooter.
	 * @param manufacturer  The manufacturer.
	 * @param model         The model.
	 * @param purchasePrice The purchase price.
	 * @param maxSpeed      The maximum speed.
	 */
	public ElectricScooter(String id, String manufacturer, String model, double purchasePrice, int maxSpeed) {
		super(id, manufacturer, model, purchasePrice);
		this.maxSpeed = maxSpeed;
	}

	@Override
	public VehicleType getType() {
		return VehicleType.SCOOTER;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}
}
