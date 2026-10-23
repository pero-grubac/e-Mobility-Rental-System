package net.etfbl.pj2.model;

/**
 * An electric bike, with the distance it can travel on one charge.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class ElectricBike extends TransportVehicle {
	private static final long serialVersionUID = 2L;

	private final int rangePerCharge;

	/**
	 * Constructs a new electric bike with a fully charged battery.
	 *
	 * @param id             The unique ID of the bike.
	 * @param manufacturer   The manufacturer.
	 * @param model          The model.
	 * @param purchasePrice  The purchase price.
	 * @param rangePerCharge The range on one charge (autonomy).
	 */
	public ElectricBike(String id, String manufacturer, String model, double purchasePrice, int rangePerCharge) {
		super(id, manufacturer, model, purchasePrice);
		this.rangePerCharge = rangePerCharge;
	}

	@Override
	public VehicleType getType() {
		return VehicleType.BIKE;
	}

	public int getRangePerCharge() {
		return rangePerCharge;
	}
}
