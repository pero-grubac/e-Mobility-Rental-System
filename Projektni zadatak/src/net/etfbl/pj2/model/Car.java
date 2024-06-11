package net.etfbl.pj2.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import net.etfbl.pj2.resources.AppConfig;

/**
 * A class representing a car, extending the {@code TransportVehicle} class. It
 * provides additional attributes and functionalities specific to cars.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Car extends TransportVehicle {

	private static final long serialVersionUID = 1L;
	private LocalDate purchaseDate;
	private String description;
	private boolean multipleUsers=true;

	/**
	 * Default constructor for the {@code Car} class.
	 */
	public Car() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Parameterized constructor for the {@code Car} class.
	 *
	 * @param id                the unique identifier of the car
	 * @param manufacturer      the manufacturer of the car
	 * @param model             the model of the car
	 * @param purchasePrice     the purchase price of the car
	 * @param batteryLevel      the battery level of the car
	 * @param purchaseDate      the purchase date of the car
	 * @param description       the description of the car
	 * @param passengerCapacity the passenger capacity of the car
	 */
	public Car(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel,
			LocalDate purchaseDate, String description, Integer passengerCapacity) {

		super(id, manufacturer, model, purchasePrice, batteryLevel);
		this.purchaseDate = purchaseDate;
		this.description = description;

	}

	/**
	 * Parameterized constructor for the {@code Car} class without passenger
	 * capacity.
	 *
	 * @param id            the unique identifier of the car
	 * @param manufacturer  the manufacturer of the car
	 * @param model         the model of the car
	 * @param purchasePrice the purchase price of the car
	 * @param purchaseDate  the purchase date of the car
	 * @param description   the description of the car
	 */
	public Car(String id, String manufacturer, String model, Double purchasePrice, LocalDate purchaseDate,
			String description) {

		super(id, manufacturer, model, purchasePrice);
		this.purchaseDate = purchaseDate;
		this.description = description;

	}

	/**
	 * Charges the battery of the car to its maximum level.
	 */
	@Override
	public void chargeBattery() {
		setBatteryLevel(getCONF().getBatteryMaxLevel());
	}

	/**
	 * Drains the battery of the car by a specified amount. If the battery level
	 * drops below the minimum level, it charges the battery.
	 */
	@Override
	public void drainBattery() {
		if (getBatteryLevel() == null
				|| (getBatteryLevel() - getCONF().getCarBatteryDrain()) <= getCONF().getBatteryMinLevel())
			chargeBattery();
		setBatteryLevel(getBatteryLevel() - getCONF().getCarBatteryDrain());

	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Car " + super.toString() + ", purchaseDate= "
				+ (purchaseDate != null ? purchaseDate.format(getDateFormatter()) : "N/A") + ", description= "
				+ description;
	}

}
