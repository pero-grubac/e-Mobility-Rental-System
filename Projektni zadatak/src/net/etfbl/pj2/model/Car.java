package net.etfbl.pj2.model;

import java.util.Date;

public class Car extends TransportVehicle {
	
	private Date purchaseDate;
	private String description;
	private Integer passengerCapacity;

	@Override
	public void chargeBattery() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drainBattery() {
		// TODO Auto-generated method stub
		
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPassengerCapacity() {
		return passengerCapacity;
	}

	public void setPassengerCapacity(Integer passengerCapacity) {
		this.passengerCapacity = passengerCapacity;
	}
	

}
