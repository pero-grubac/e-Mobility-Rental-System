package net.etfbl.pj2.model;

public abstract class TransportVehicle implements Chargeable {

	private String id;
	private String manufacturer;
	private String model;
	private String purchasePrice;
	private String batteryLevel;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public String getBatteryLevel() {
		return batteryLevel;
	}
	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	
	 

	
}
