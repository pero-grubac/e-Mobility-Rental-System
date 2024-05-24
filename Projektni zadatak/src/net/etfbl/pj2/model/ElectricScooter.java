package net.etfbl.pj2.model;

import java.io.Serializable;

public class ElectricScooter extends TransportVehicle  {
	private Integer maxSpeed;

	public ElectricScooter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ElectricScooter(String id, String manufacturer, String model, Double purchasePrice, String batteryLevel,
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
		// TODO Auto-generated method stub

	}

	@Override
	public void drainBattery() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "ElectricScooter " + super.toString() + ", maxSpeed= " + maxSpeed;
	}

}
