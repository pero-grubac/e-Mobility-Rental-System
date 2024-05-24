package net.etfbl.pj2.model;

import java.io.Serializable;

public class ElectricBike extends TransportVehicle {
	private Integer rangePerCharge;

	public ElectricBike() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ElectricBike(String id, String manufacturer, String model, Double purchasePrice, String batteryLevel,
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
		// TODO Auto-generated method stub

	}

	@Override
	public void drainBattery() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "ElectricBike " + super.toString() + ", rangePerCharge= " + rangePerCharge;
	}

}
