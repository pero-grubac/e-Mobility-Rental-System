package net.etfbl.pj2.model;

import java.io.Serializable;

import net.etfbl.pj2.resources.AppConfig;

public class ElectricBike extends TransportVehicle {
	
	private static final long serialVersionUID = 1L;
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
		setBatteryLevel(getCONF().getBatteryMaxLevel());

	}

	@Override
	public void drainBattery() {
		if (getBatteryLevel() == null || (getBatteryLevel() - getCONF().getBikeBatteryDrain()) <= getCONF().getBatteryMinLevel())
			chargeBattery();
		setBatteryLevel(getBatteryLevel() - getCONF().getBikeBatteryDrain());


	}

	@Override
	public String toString() {
		return "ElectricBike " + super.toString() + ", rangePerCharge= " + rangePerCharge;
	}

}
