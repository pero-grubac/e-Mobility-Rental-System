package net.etfbl.pj2.model;

import java.time.LocalDate;

/**
 * An electric car. Besides the common data it has a purchase date and a
 * description, and it can carry more than one person.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Car extends TransportVehicle {
	private static final long serialVersionUID = 2L;

	private final LocalDate purchaseDate;
	private final String description;

	/**
	 * Constructs a new car with a fully charged battery.
	 *
	 * @param id            The unique ID of the car.
	 * @param manufacturer  The manufacturer.
	 * @param model         The model.
	 * @param purchasePrice The purchase price.
	 * @param purchaseDate  The purchase date, or null if it is unknown.
	 * @param description   The description.
	 */
	public Car(String id, String manufacturer, String model, double purchasePrice, LocalDate purchaseDate,
			String description) {
		super(id, manufacturer, model, purchasePrice);
		this.purchaseDate = purchaseDate;
		this.description = description;
	}

	@Override
	public VehicleType getType() {
		return VehicleType.CAR;
	}

	@Override
	public boolean canCarryMultiplePeople() {
		return true;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public String getDescription() {
		return description;
	}
}
