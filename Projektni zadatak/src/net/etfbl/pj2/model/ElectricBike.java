package net.etfbl.pj2.model;

public class ElectricBike extends TransportVehicle {
	private String rangePerCharge;

	@Override
	public void chargeBattery() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drainBattery() {
		// TODO Auto-generated method stub
		
	}

	public String getRangePerCharge() {
		return rangePerCharge;
	}

	public void setRangePerCharge(String rangePerCharge) {
		this.rangePerCharge = rangePerCharge;
	}

}
