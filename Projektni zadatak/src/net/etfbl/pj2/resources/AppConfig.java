package net.etfbl.pj2.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents the application configuration with properties loaded from the
 * "app.properties" file.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class AppConfig {
	private Properties properties;

	/**
	 * Constructs a new AppConfig object and initializes it by loading properties
	 * from the "app.properties" file.
	 */
	public AppConfig() {
		properties = new Properties();
		loadProperties();
	}

	/**
	 * Loads properties from the "app.properties" file.
	 */
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
		} catch (Exception ex) {
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

	public Integer getTableXMin() {
		return getIntegerProperty("TABLE_X_MIN");
	}

	public Integer getTableXMax() {
		return getIntegerProperty("TABLE_X_MAX");
	}

	public Integer getTableYMin() {
		return getIntegerProperty("TABLE_Y_MIN");
	}

	public Integer getTableYMax() {
		return getIntegerProperty("TABLE_Y_MAX");
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

	public String getSummaryReportFileName() {
		return getProperty("SUMMARY_REPORT_FILE");
	}

	public String getSpecialReportFileName() {
		return getProperty("SPECIAL_REPORT_FILE");
	}

	public Double getDefaultUnitPrice() {
		return getDoubleProperty("DEFAUL_UNIT_PRICE");
	}

	public Integer getNarrowBeginingXAxis() {
		return getIntegerProperty("NARROW_X_MIN");
	}

	public String getYes() {
		return getProperty("YES");
	}

	public String getNo() {
		return getProperty("NO");
	}

	public Integer getNarrowBeginingYAxis() {
		return getIntegerProperty("NARROW_Y_MIN");
	}

	public Integer getNarrowEndXAxis() {
		return getIntegerProperty("NARROW_X_MAX");
	}

	public Integer getNarrowEndYAxis() {
		return getIntegerProperty("NARROW_Y_MAX");
	}

	public Double getBreakDownUnitPrice() {
		return getDoubleProperty("BREAKDOWN_UNIT_PRICE");
	}

	public Double getBatteryMaxLevel() {
		return getDoubleProperty("BATTERY_MAX_LEVEL");
	}

	public Double getBatteryMinLevel() {
		return getDoubleProperty("BATTERY_MIN_LEVEL");
	}

	public Double getCarBatteryDrain() {
		return getDoubleProperty("CAR_BATTERY_DRAIN");
	}

	public Double getBikeBatteryDrain() {
		return getDoubleProperty("BIKE_BATTERY_DRAIN");
	}

	public Double getScooterBatteryDrain() {
		return getDoubleProperty("SCOOTER_BATTERY_DRAIN");
	}

	public Integer getPauseBetweenTime() {
		return getIntegerProperty("PAUSE_BETWEEN_TIME");
	}

	public String getIconsFolder() {
		return getProperty("ICONS_FOLDER");
	}

	public String getFrameIcon() {
		return getProperty("FRAME_ICON_IMAGE");
	}

	public String getDailyReportIcon() {
		return getProperty("DAILY_REPORT_ICON");
	}

	public String getSummaryReportIcon() {
		return getProperty("SUMMARY_REPORT_ICON");
	}

	public String getSpecialReportIcon() {
		return getProperty("SPECIAL_REPORT_ICON");
	}

	public Integer getMaxDriveTimePerUnit() {
		return getIntegerProperty("MAX_DRIVE_TIME_PER_UNIIT_MS");
	}

	public String getColumnType() {
		return getProperty("COLUMN_TYPE");
	}

	public String getColumnId() {
		return getProperty("COLUMN_ID");
	}

	public String getColumnManufacturer() {
		return getProperty("COLUMN_MANUFACTURER");
	}

	public String getColumnModel() {
		return getProperty("COLUMN_MODEL");
	}

	public String getColumnMaxSpeed() {
		return getProperty("COLUMN_MEX_SPEED");
	}

	public String getColumnRamgePerCharge() {
		return getProperty("COLUMN_RANGE_PER_CHARGE");
	}

	public String getColumnRevenue() {
		return getProperty("COLUM_REVENUE");
	}

	public String getColumnPurchaseDate() {
		return getProperty("COLUMN_PURCHASE_DATE");
	}

	public String getColumnPurchasePrice() {
		return getProperty("COLUM_PURCHASE_PRICE");
	}

	public String getColumnDescription() {
		return getProperty("COLUMN_DESCRIPTION");
	}

	public String getColumnBatteryLevel() {
		return getProperty("COLUMN_BATTERY_LEVEL");
	}

	public String getCarColor() {
		return getProperty("CAR_COLOR");
	}

	public String getBikeColor() {
		return getProperty("BIKE_COLOR");
	}

	public String getScooterColor() {
		return getProperty("SCOOTER_COLOR");
	}

	public String getWideAreaColor() {
		return getProperty("WIDE_ARE_COLOR");
	}

	public String getNarrowColor() {
		return getProperty("NARROOW_AREA_COLOR");
	}

	public String getBackgroundColor() {
		return getProperty("BACKGROUND_COLOR");
	}

	public String getLabelTime() {
		return getProperty("LABEL_TIME");
	}

	public String getLabelDate() {
		return getProperty("LABEL_DATE");
	}

	public String getDailyReportName() {
		return getProperty("DAILY_REPORT_NAME");
	}

	public String getSummaryReportName() {
		return getProperty("SUMMARY_REPORT_NAME");
	}

	public String getSpecialReportName() {
		return getProperty("SPECIAL_REPORT_NAME");
	}

	public String getMenuReport() {
		return getProperty("MENU_REPORT");
	}

	public String getColumnTotalIncome() {
		return getProperty("COLUMN_TOTAL_INCOME");
	}

	public String getColumnTotalDiscount() {
		return getProperty("COLUMN_TOTAL_DISCOUNT");
	}

	public String getColumnTotalPromotion() {
		return getProperty("COLUMN_TOTAL_PROMOTION");
	}

	public String getColumnWideAreaIncome() {
		return getProperty("COLUMN_WIDE_AREA_INCOME");
	}

	public String getColumnnNarrowAreaIncome() {
		return getProperty("COLUMN_NARROW_AREA_INCOME");
	}

	public String getColumnMaintenance() {
		return getProperty("COLUM_MAINTENANCE");
	}

	public String getColumnRepairs() {
		return getProperty("COLUMN_REPAIRS");
	}

	public String getColumnTax() {
		return getProperty("COLUMN_TOTAL_TAX");
	}

	public String getColumnCost() {
		return getProperty("COLUMN_TOTAL_COST");
	}

	public String getAppTitle() {
		return getProperty("APP_TITLE");
	}

	public String getCarName() {
		return getProperty("CAR_NAME");
	}

	public String getScooterName() {
		return getProperty("BIKE_NAME");
	}

	public String getBikeName() {
		return getProperty("SCOOTER_NAME");
	}

	public String getEndMessager() {
		return getProperty("END_MESSAGE");
	}
}
