package net.etfbl.pj2.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
	private Properties properties;

	public AppConfig() {
		properties = new Properties();
		loadProperties();
	}

	private void loadProperties() {
		try (InputStream input = getClass().getClassLoader()
				.getResourceAsStream("net/etfbl/pj2/resources/app.properties")) {
			if (input == null) {
				System.out.println("Sorry, unable to find app.properties");
				return;
			}
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public double getDoubleProperty(String key) {
		return Double.parseDouble(properties.getProperty(key));
	}

	public Integer getIntegerProperty(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	public double getCarUnitPrice() {
		return getDoubleProperty("CAR_UNIT_PRICE");
	}

	public double getBikeUnitPrice() {
		return getDoubleProperty("BIKE_UNIT_PRICE");
	}

	public double getScooterUnitPrice() {
		return getDoubleProperty("SCOOTER_UNIT_PRICE");
	}

	public double getDistanceNarrow() {
		return getDoubleProperty("DISTANCE_NARROW");
	}

	public double getDistanceWide() {
		return getDoubleProperty("DISTANCE_WIDE");
	}

	public double getDiscount() {
		return getDoubleProperty("DISCOUNT");
	}

	public double getDiscountProm() {
		return getDoubleProperty("DISCOUNT_PROM");
	}

	public Integer gerUserDocLength() {
		return getIntegerProperty("USER_DOC_LENGTH");
	}

	public Integer getTableLength() {
		return getIntegerProperty("TABLE_LENGTH");
	}

	public Integer getTableWidth() {
		return getIntegerProperty("TABLE_WIDTH");
	}

	public Integer getBreakdownReasonLength() {
		return getIntegerProperty("BREAKDOWN_REASON_LENGTH");
	}

	public String getTestFolder() {
		return getProperty("TEST_FOLDER");
	}

	public String getInvoiceFolder() {
		return getProperty("INVOICE_FOLDER");
	}

	public String getTestRental() {
		return getProperty("TEST_RENTAL");
	}

	public String getTestVehicle() {
		return getProperty("TEST_VEHICLE");
	}

	public String getInvoiceDocType() {
		return getProperty("INVOICE_DOC_TYPE");
	}

	public Double getMaintenceCost() {
		return getDoubleProperty("MAINTENCE_COST");
	}

	public Double getTotalCostPer() {
		return getDoubleProperty("TOTAL_COST_PER");
	}

	public Integer getBigDecimalRound() {
		return getIntegerProperty("BIGDECIMAL_ROUND");
	}

	public Double getCarRepairPrice() {
		return getDoubleProperty("CAR_REPAIR_PRICE");
	}

	public Double getBikeRepairPrice() {
		return getDoubleProperty("BIKE_REPAIR_PRICE");
	}

	public Double getScooterRepairPrice() {
		return getDoubleProperty("SCOOTER_REPAIR_PRICE");
	}

	public Double getTax() {
		return getDoubleProperty("TAX");
	}

	public String getReportDocType() {
		return getProperty("REPORT_DOC_TYPE");
	}

	public String getReportFolder() {
		return getProperty("REPORT_FOLDER");
	}

	public String getDailyReportFileName() {
		return getProperty("DAILY_REPORT_FILE");
	}
}
