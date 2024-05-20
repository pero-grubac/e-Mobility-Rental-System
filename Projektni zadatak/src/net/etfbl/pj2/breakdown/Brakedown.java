package net.etfbl.pj2.breakdown;

import java.util.Date;

public class Brakedown {
	private String vehicleID;
	private Date breakdownDateTime;
	private String description;
	private String vehicleType;
	
	
	public String getVehicleID() {
		return vehicleID;
	}
	public void setVehicleID(String vehicleID) {
		this.vehicleID = vehicleID;
	}
	public Date getBreakdownDateTime() {
		return breakdownDateTime;
	}
	public void setBreakdownDateTime(Date breakdownDateTime) {
		this.breakdownDateTime = breakdownDateTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	
}
