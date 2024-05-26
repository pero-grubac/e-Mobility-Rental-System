package net.etfbl.pj2.model;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class TransportVehicle implements Chargeable, Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String manufacturer;
	private String model;
	private Double purchasePrice;
	private Double batteryLevel;
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy.");

	public TransportVehicle() {
		super();
	}

	public TransportVehicle(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel) {
		super();
		this.id = id;
		this.manufacturer = manufacturer;
		this.model = model;
		this.purchasePrice = purchasePrice;
		this.batteryLevel = batteryLevel;
	}

	public TransportVehicle(String id, String manufacturer, String model, Double purchasePrice) {
		super();
		this.id = id;
		this.manufacturer = manufacturer;
		this.model = model;
		this.purchasePrice = purchasePrice;
		chargeBattery();

	}

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

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	@Override
	public String toString() {
		String batteryLevelString = (batteryLevel != null) ? String.format("%.2f%%", batteryLevel ) : "N/A";
		return "id= " + id + ", manufacturer= " + manufacturer + ", model= " + model + ", purchasePrice= "
				+ purchasePrice + ", batteryLevel= " + batteryLevelString;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TransportVehicle that = (TransportVehicle) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
