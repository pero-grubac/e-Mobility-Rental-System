package net.etfbl.pj2.resources;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.VehicleType;

/**
 * Application configuration read from the "app.properties" file on the class
 * path. Every price, rate, folder, color and text of the application comes from
 * this file, so the program can be adapted without changing the code.
 * <p>
 * The file is read only once; every instance shares the same values. Tests can
 * extend this class and override single getters, for example to redirect
 * folders.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class AppConfig {
	private static final String RESOURCE = "net/etfbl/pj2/resources/app.properties";
	private static final Properties PROPERTIES = load();

	/**
	 * Loads the properties file from the class path.
	 *
	 * @return The loaded properties.
	 */
	private static Properties load() {
		Properties properties = new Properties();
		try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(RESOURCE)) {
			if (input == null) {
				throw new IllegalStateException("Configuration file not found on the class path: " + RESOURCE);
			}
			properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new UncheckedIOException("Configuration file cannot be read: " + RESOURCE, e);
		}
		return properties;
	}

	/**
	 * Returns the raw value of a key.
	 *
	 * @param key The property key.
	 * @return The value.
	 * @throws IllegalStateException If the key is missing.
	 */
	public String getProperty(String key) {
		String value = PROPERTIES.getProperty(key);
		if (value == null) {
			throw new IllegalStateException("Missing configuration key: " + key);
		}
		return value.trim();
	}

	/**
	 * @param key The property key.
	 * @return The value as a double.
	 */
	public double getDoubleProperty(String key) {
		return Double.parseDouble(getProperty(key));
	}

	/**
	 * @param key The property key.
	 * @return The value as an int.
	 */
	public int getIntegerProperty(String key) {
		return Integer.parseInt(getProperty(key));
	}

	/**
	 * Returns a text shown in the GUI.
	 *
	 * @param key The property key, for example {@code VEHICLES_TITLE}.
	 * @return The text.
	 */
	public String getText(String key) {
		return getProperty(key);
	}

	// ---- Prices -----------------------------------------------------------

	/**
	 * @param type The vehicle type.
	 * @return The price per second of riding.
	 */
	public double getUnitPrice(VehicleType type) {
		return getDoubleProperty(type.name() + "_UNIT_PRICE");
	}

	/** @return The base price of a rental with a breakdown. */
	public double getBreakdownUnitPrice() {
		return getDoubleProperty("BREAKDOWN_UNIT_PRICE");
	}

	/** @return The distance factor of the narrow (inner) part of the city. */
	public double getDistanceNarrow() {
		return getDoubleProperty("DISTANCE_NARROW");
	}

	/** @return The distance factor of the wide (outer) part of the city. */
	public double getDistanceWide() {
		return getDoubleProperty("DISTANCE_WIDE");
	}

	/** @return The discount rate of every N-th rental. */
	public double getDiscount() {
		return getDoubleProperty("DISCOUNT");
	}

	/** @return N, so that every N-th rental gets the discount. */
	public int getDiscountEveryNthRental() {
		return getIntegerProperty("DISCOUNT_EVERY_NTH_RENTAL");
	}

	/** @return The promotion rate. */
	public double getDiscountProm() {
		return getDoubleProperty("DISCOUNT_PROM");
	}

	/**
	 * @param type The vehicle type.
	 * @return The share of the purchase price that one repair costs.
	 */
	public double getRepairCoefficient(VehicleType type) {
		return getDoubleProperty(type.name() + "_REPAIR_COEFFICIENT");
	}

	// ---- Business results ---------------------------------------------------

	/** @return Maintenance as a share of the total income. */
	public double getMaintenanceRate() {
		return getDoubleProperty("MAINTENANCE_RATE");
	}

	/** @return Company costs as a share of the total income. */
	public double getCompanyCostRate() {
		return getDoubleProperty("COMPANY_COST_RATE");
	}

	/** @return The tax rate. */
	public double getTaxRate() {
		return getDoubleProperty("TAX_RATE");
	}

	/** @return The number of decimals of money amounts. */
	public int getMoneyScale() {
		return getIntegerProperty("MONEY_SCALE");
	}

	// ---- Map --------------------------------------------------------------

	/** @return The smallest x coordinate of the map. */
	public int getMapXMin() {
		return getIntegerProperty("MAP_X_MIN");
	}

	/** @return The largest x coordinate of the map. */
	public int getMapXMax() {
		return getIntegerProperty("MAP_X_MAX");
	}

	/** @return The smallest y coordinate of the map. */
	public int getMapYMin() {
		return getIntegerProperty("MAP_Y_MIN");
	}

	/** @return The largest y coordinate of the map. */
	public int getMapYMax() {
		return getIntegerProperty("MAP_Y_MAX");
	}

	/**
	 * @param field A field.
	 * @return True if the field lies on the map.
	 */
	public boolean isOnMap(Field field) {
		return field.getX() >= getMapXMin() && field.getX() <= getMapXMax() && field.getY() >= getMapYMin()
				&& field.getY() <= getMapYMax();
	}

	/**
	 * @param field A field.
	 * @return True if the field is in the narrow (inner) part of the city.
	 */
	public boolean isNarrowArea(Field field) {
		return field.getX() >= getIntegerProperty("NARROW_X_MIN") && field.getX() <= getIntegerProperty("NARROW_X_MAX")
				&& field.getY() >= getIntegerProperty("NARROW_Y_MIN")
				&& field.getY() <= getIntegerProperty("NARROW_Y_MAX");
	}

	// ---- Vehicles ---------------------------------------------------------

	/** @return The battery level of a fully charged vehicle. */
	public double getBatteryMaxLevel() {
		return getDoubleProperty("BATTERY_MAX_LEVEL");
	}

	/** @return The lowest battery level. */
	public double getBatteryMinLevel() {
		return getDoubleProperty("BATTERY_MIN_LEVEL");
	}

	/**
	 * @param type The vehicle type.
	 * @return The battery used for every field driven.
	 */
	public double getBatteryDrain(VehicleType type) {
		return getDoubleProperty(type.name() + "_BATTERY_DRAIN");
	}

	/**
	 * @param type The vehicle type.
	 * @return The name of the type shown to people.
	 */
	public String getVehicleTypeName(VehicleType type) {
		return getProperty(type.name() + "_NAME");
	}

	/**
	 * @param type The vehicle type.
	 * @return The value of the type column in the vehicles CSV file.
	 */
	public String getCsvVehicleType(VehicleType type) {
		return getProperty(type.name() + "_CSV_TYPE");
	}

	/**
	 * @param type The vehicle type.
	 * @return The color of the type, as a hex code.
	 */
	public String getVehicleColor(VehicleType type) {
		return getProperty(type.name() + "_COLOR");
	}

	// ---- Simulation -------------------------------------------------------

	/** @return The pause between two time slots, in seconds. */
	public int getPauseBetweenTimeSlots() {
		return getIntegerProperty("PAUSE_BETWEEN_TIME_SLOTS");
	}

	// ---- Input ------------------------------------------------------------

	/** @return The path of the rentals CSV file. */
	public Path getRentalsFile() {
		return Paths.get(getDataFolder(), getProperty("RENTALS_FILE"));
	}

	/** @return The path of the vehicles CSV file. */
	public Path getVehiclesFile() {
		return Paths.get(getDataFolder(), getProperty("VEHICLES_FILE"));
	}

	/** @return The folder with the input files. */
	public String getDataFolder() {
		return getProperty("DATA_FOLDER");
	}

	/** @return The format of the rental date and time. */
	public DateTimeFormatter getRentalDateTimeFormatter() {
		return DateTimeFormatter.ofPattern(getProperty("RENTAL_DATE_TIME_FORMAT"));
	}

	/** @return The format of the purchase date of a car. */
	public DateTimeFormatter getPurchaseDateFormatter() {
		return DateTimeFormatter.ofPattern(getProperty("PURCHASE_DATE_FORMAT"));
	}

	/** @return The word for "yes" in the input files. */
	public String getYes() {
		return getProperty("YES");
	}

	/** @return The word for "no" in the input files. */
	public String getNo() {
		return getProperty("NO");
	}

	// ---- Users and breakdowns ---------------------------------------------

	/** @return The length of generated document numbers. */
	public int getUserDocumentLength() {
		return getIntegerProperty("USER_DOCUMENT_LENGTH");
	}

	/** @return The share of car users who are foreign citizens. */
	public double getForeignUserShare() {
		return getDoubleProperty("FOREIGN_USER_SHARE");
	}

	/** @return The possible reasons of a breakdown. */
	public List<String> getBreakdownReasons() {
		return Arrays.stream(getProperty("BREAKDOWN_REASONS").split(";")).map(String::trim)
				.filter(s -> !s.isEmpty()).toList();
	}

	// ---- Output -----------------------------------------------------------

	/** @return The folder for invoices. */
	public String getInvoiceFolder() {
		return getProperty("INVOICE_FOLDER");
	}

	/** @return The folder for reports. */
	public String getReportFolder() {
		return getProperty("REPORT_FOLDER");
	}

	/** @return The folder for the serialized special report. */
	public Path getSpecialReportFolder() {
		return Paths.get(getReportFolder(), getProperty("SPECIAL_REPORT_FOLDER"));
	}

	/** @return The extension of text files, with the dot. */
	public String getTextFileExtension() {
		return getProperty("TEXT_FILE_EXTENSION");
	}

	/** @return The extension of serialized files, with the dot. */
	public String getBinaryFileExtension() {
		return getProperty("BINARY_FILE_EXTENSION");
	}

	/** @return The file name prefix of daily reports. */
	public String getDailyReportFileName() {
		return getProperty("DAILY_REPORT_FILE");
	}

	/** @return The file name of the summary report. */
	public String getSummaryReportFileName() {
		return getProperty("SUMMARY_REPORT_FILE");
	}

	/** @return The date format used in report file names. */
	public DateTimeFormatter getFileDateFormatter() {
		return DateTimeFormatter.ofPattern(getProperty("FILE_DATE_FORMAT"));
	}

	/** @return The date and time format used in invoice file names. */
	public DateTimeFormatter getInvoiceFileDateTimeFormatter() {
		return DateTimeFormatter.ofPattern(getProperty("INVOICE_FILE_DATE_TIME_FORMAT"));
	}

	// ---- GUI --------------------------------------------------------------

	/** @return The class name of the Swing look and feel. */
	public String getLookAndFeel() {
		return getProperty("LOOK_AND_FEEL");
	}

	/**
	 * @param key The key of an icon file name, for example {@code APP_ICON}.
	 * @return The path of the icon.
	 */
	public String getIconPath(String key) {
		return Paths.get(getProperty("ICONS_FOLDER"), getProperty(key)).toString();
	}

	/** @return How often the secondary screens refresh, in milliseconds. */
	public int getScreenRefreshMillis() {
		return getIntegerProperty("SCREEN_REFRESH_MS");
	}

	/**
	 * @param key The key of a color, for example {@code ACCENT_COLOR}.
	 * @return The color.
	 */
	public Color getColor(String key) {
		return Color.decode(getProperty(key));
	}
}
