package net.etfbl.pj2.model;

/**
 * The kinds of vehicles the company rents out. The name of each constant is
 * also the prefix of its keys in app.properties (for example
 * {@code CAR_UNIT_PRICE} or {@code SCOOTER_COLOR}).
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public enum VehicleType {
	/** Electric car. */
	CAR,
	/** Electric bike. */
	BIKE,
	/** Electric scooter. */
	SCOOTER
}
