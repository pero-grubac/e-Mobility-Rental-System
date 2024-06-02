package net.etfbl.pj2.model;

import java.io.Serializable;

import net.etfbl.pj2.resources.AppConfig;

/**
 * A class representing an electric bike, which is a type of transport vehicle.
 * It extends the TransportVehicle class and implements the Chargeable
 * interface. Electric bikes have a specific range per charge, indicating how
 * far they can travel on a single battery charge.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class ElectricBike extends TransportVehicle {

	private static final long serialVersionUID = 1L;
	private Integer rangePerCharge;

	/**
	 * Default constructor for creating an ElectricBike object with default values.
	 */
	public ElectricBike() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs an ElectricBike object with the specified attributes.
	 * 
	 * @param id             The ID of the electric bike.
	 * @param manufacturer   The manufacturer of the electric bike.
	 * @param model          The model of the electric bike.
	 * @param purchasePrice  The purchase price of the electric bike.
	 * @param batteryLevel   The current battery level of the electric bike.
	 * @param rangePerCharge The range per charge of the electric bike.
	 */
	public ElectricBike(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel,
			Integer rangePerCharge) {
		super(id, manufacturer, model, purchasePrice, batteryLevel);
		this.rangePerCharge = rangePerCharge;
	}

	/**
	 * Constructs an ElectricBike object with the specified attributes and default
	 * battery level.
	 * 
	 * @param id             The ID of the electric bike.
	 * @param manufacturer   The manufacturer of the electric bike.
	 * @param model          The model of the electric bike.
	 * @param purchasePrice  The purchase price of the electric bike.
	 * @param rangePerCharge The range per charge of the electric bike.
	 */
	public ElectricBike(String id, String manufacturer, String model, Double purchasePrice, Integer rangePerCharge) {
		super(id, manufacturer, model, purchasePrice);
		this.rangePerCharge = rangePerCharge;
	}

	/**
	 * Charges the battery of the car to its maximum level.
	 */
	@Override
	public void chargeBattery() {
		setBatteryLevel(getCONF().getBatteryMaxLevel());

	}

	/**
	 * Drains the battery of the electric bike by a specified amount. If the battery
	 * level drops below the minimum level, it charges the battery.
	 */
	@Override
	public void drainBattery() {
		if (getBatteryLevel() == null
				|| (getBatteryLevel() - getCONF().getBikeBatteryDrain()) <= getCONF().getBatteryMinLevel())
			chargeBattery();
		setBatteryLevel(getBatteryLevel() - getCONF().getBikeBatteryDrain());

	}

	public Integer getRangePerCharge() {
		return rangePerCharge;
	}

	public void setRangePerCharge(Integer rangePerCharge) {
		this.rangePerCharge = rangePerCharge;
	}

	@Override
	public String toString() {
		return "ElectricBike " + super.toString() + ", rangePerCharge= " + rangePerCharge;
	}

}
