package net.etfbl.pj2.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import net.etfbl.pj2.resources.AppConfig;

public class Car extends TransportVehicle {

	private static final long serialVersionUID = 1L;
	private LocalDate purchaseDate;
	private String description;

	public Car(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel,
			LocalDate purchaseDate, String description, Integer passengerCapacity) {

		super(id, manufacturer, model, purchasePrice, batteryLevel);
		this.purchaseDate = purchaseDate;
		this.description = description;

	}

	public Car(String id, String manufacturer, String model, Double purchasePrice, LocalDate purchaseDate,
			String description) {

		super(id, manufacturer, model, purchasePrice);
		this.purchaseDate = purchaseDate;
		this.description = description;

	}

	@Override
	public void chargeBattery() {
		setBatteryLevel(getCONF().getBatteryMaxLevel());
	}

	@Override
	public void drainBattery() {
		if (getBatteryLevel() == null || (getBatteryLevel() - getCONF().getCarBatteryDrain()) <= getCONF().getBatteryMinLevel())
			chargeBattery();
		setBatteryLevel(getBatteryLevel() - getCONF().getCarBatteryDrain());

	}

	@Override
	public String toString() {
		return "Car " + super.toString() + ", purchaseDate= "
				+ (purchaseDate != null ? purchaseDate.format(getDateFormatter()) : "N/A") + ", description= "
				+ description;
	}

}
