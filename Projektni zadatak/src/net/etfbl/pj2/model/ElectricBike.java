package net.etfbl.pj2.model;

import java.io.Serializable;

import net.etfbl.pj2.resources.AppConfig;

public class ElectricBike extends TransportVehicle {
	private Integer rangePerCharge;

	public ElectricBike() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ElectricBike(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel,
			Integer rangePerCharge) {
		super(id, manufacturer, model, purchasePrice, batteryLevel);
		this.rangePerCharge = rangePerCharge;
	}

	public ElectricBike(String id, String manufacturer, String model, Double purchasePrice, Integer rangePerCharge) {
		super(id, manufacturer, model, purchasePrice);
		this.rangePerCharge = rangePerCharge;
	}

	@Override
	public void chargeBattery() {
		AppConfig conf = new AppConfig();
		setBatteryLevel(conf.getBatteryMaxLevel());

	}

	@Override
	public void drainBattery() {
		AppConfig conf = new AppConfig();
		if (getBatteryLevel() == null || getBatteryLevel() <= conf.getBatteryMinLevel())
			chargeBattery();
		setBatteryLevel(getBatteryLevel() - conf.getBikeBatteryDrain());


	}

	@Override
	public String toString() {
		return "ElectricBike " + super.toString() + ", rangePerCharge= " + rangePerCharge;
	}

}
