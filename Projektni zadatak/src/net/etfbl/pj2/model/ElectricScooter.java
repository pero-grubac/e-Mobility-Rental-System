package net.etfbl.pj2.model;

import java.io.Serializable;

import net.etfbl.pj2.resources.AppConfig;

public class ElectricScooter extends TransportVehicle {
	private Integer maxSpeed;

	public ElectricScooter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ElectricScooter(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel,
			Integer maxSpeed) {
		super(id, manufacturer, model, purchasePrice, batteryLevel);
		this.maxSpeed = maxSpeed;
	}

	public ElectricScooter(String id, String manufacturer, String model, Double purchasePrice, Integer maxSpeed) {
		super(id, manufacturer, model, purchasePrice);
		this.maxSpeed = maxSpeed;
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
		setBatteryLevel(getBatteryLevel() - conf.getScooterBatteryDrain());

	}

	@Override
	public String toString() {
		return "ElectricScooter " + super.toString() + ", maxSpeed= " + maxSpeed;
	}

}
