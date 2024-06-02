package net.etfbl.pj2.model;

import java.io.Serializable;

import net.etfbl.pj2.resources.AppConfig;

/**
 * A class representing an electric scooter, which is a type of transport
 * vehicle. It extends the TransportVehicle class and implements the Chargeable
 * interface. Electric scooters have a maximum speed and can be charged and
 * drained of battery.
 * 
 *  @author Pero Grubač
 * @since 2.6.2024.
 */
public class ElectricScooter extends TransportVehicle {

	private static final long serialVersionUID = 1L;
	private Integer maxSpeed;

	/**
	 * Default constructor for creating an ElectricScooter object with default
	 * values.
	 */
	public ElectricScooter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs an ElectricScooter object with the specified attributes.
	 * 
	 * @param id            The ID of the electric scooter.
	 * @param manufacturer  The manufacturer of the electric scooter.
	 * @param model         The model of the electric scooter.
	 * @param purchasePrice The purchase price of the electric scooter.
	 * @param batteryLevel  The current battery level of the electric scooter.
	 * @param maxSpeed      The maximum speed of the electric scooter.
	 */
	public ElectricScooter(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel,
			Integer maxSpeed) {
		super(id, manufacturer, model, purchasePrice, batteryLevel);
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Constructs an ElectricScooter object with the specified attributes and
	 * default battery level.
	 * 
	 * @param id            The ID of the electric scooter.
	 * @param manufacturer  The manufacturer of the electric scooter.
	 * @param model         The model of the electric scooter.
	 * @param purchasePrice The purchase price of the electric scooter.
	 * @param maxSpeed      The maximum speed of the electric scooter.
	 */
	public ElectricScooter(String id, String manufacturer, String model, Double purchasePrice, Integer maxSpeed) {
		super(id, manufacturer, model, purchasePrice);
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Charges the battery of the car to its maximum level.
	 */
	@Override
	public void chargeBattery() {

		setBatteryLevel(getCONF().getBatteryMaxLevel());

	}

	/**
	 * Drains the battery of the electric scooter by a specified amount. If the
	 * battery level drops below the minimum level, it charges the battery.
	 */
	@Override
	public void drainBattery() {
		if (getBatteryLevel() == null
				|| (getBatteryLevel() - getCONF().getScooterBatteryDrain()) <= getCONF().getBatteryMinLevel())
			chargeBattery();
		setBatteryLevel(getBatteryLevel() - getCONF().getScooterBatteryDrain());

	}

	public Integer getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Integer maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	@Override
	public String toString() {
		return "ElectricScooter " + super.toString() + ", maxSpeed= " + maxSpeed;
	}

}
