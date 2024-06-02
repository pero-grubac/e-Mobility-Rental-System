package net.etfbl.pj2.model;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import net.etfbl.pj2.resources.AppConfig;
/**
 * Represents a transport vehicle that can be charged and drained.
 * This is an abstract class that implements the Chargeable interface and is Serializable.
 * 
 *  @author Pero Grubač
 * @since 2.6.2024.
 */
public abstract class TransportVehicle implements Chargeable, Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String manufacturer;
	private String model;
	private Double purchasePrice;
	private Double batteryLevel;
	private static transient final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy.");
	private static transient AppConfig CONF = new AppConfig();
	 /**
     * Constructs a new TransportVehicle object with default values.
     */
	public TransportVehicle() {
		super();
	}
	 /**
     * Constructs a new TransportVehicle object with the specified parameters.
     * 
     * @param id The ID of the vehicle.
     * @param manufacturer The manufacturer of the vehicle.
     * @param model The model of the vehicle.
     * @param purchasePrice The purchase price of the vehicle.
     * @param batteryLevel The battery level of the vehicle.
     */
	public TransportVehicle(String id, String manufacturer, String model, Double purchasePrice, Double batteryLevel) {
		super();
		this.id = id;
		this.manufacturer = manufacturer;
		this.model = model;
		this.purchasePrice = purchasePrice;
		this.batteryLevel = batteryLevel;
	}
	  /**
     * Constructs a new TransportVehicle object with the specified parameters,
     * setting the battery level to a default value and charging it.
     * 
     * @param id The ID of the vehicle.
     * @param manufacturer The manufacturer of the vehicle.
     * @param model The model of the vehicle.
     * @param purchasePrice The purchase price of the vehicle.
     */
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

	public static AppConfig getCONF() {
		return CONF;
	}

	public static void setCONF(AppConfig cONF) {
		CONF = cONF;
	}

	@Override
	public String toString() {
		String batteryLevelString = (batteryLevel != null) ? String.format("%.2f%%", batteryLevel ) : "0";
		return "id= " + id + ", manufacturer= " + manufacturer + ", model= " + model + ", purchasePrice= "
				+ purchasePrice + ", batteryLevel= " + batteryLevelString;
	}

	public static DateTimeFormatter getDateFormatter() {
		return DATE_FORMATTER;
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
