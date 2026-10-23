package net.etfbl.pj2.invoice;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import net.etfbl.pj2.breakdown.Breakdown;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.User;
import net.etfbl.pj2.resources.AppConfig;

/**
 * The invoice of one rental. All prices are calculated when the invoice is
 * created:
 * <ul>
 * <li>base price = unit price of the vehicle type * duration in seconds (0 if
 * the vehicle broke down),</li>
 * <li>amount = base price * DISTANCE_WIDE if at least one field of the route is
 * in the wide part of the city, otherwise * DISTANCE_NARROW,</li>
 * <li>discount = DISCOUNT * amount for every N-th rental,</li>
 * <li>promotion = DISCOUNT_PROM * amount for rentals with a promotion,</li>
 * <li>total = amount - discount - promotion.</li>
 * </ul>
 * The battery levels and the breakdown are filled in by the simulation, which
 * then writes the invoice to a text file.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Invoice {
	private static final int LABEL_WIDTH = 22;
	private static final String LINE = "-".repeat(46);
	private static final String DOUBLE_LINE = "=".repeat(46);

	private final Rental rental;
	private final TransportVehicle vehicle;
	private final double unitPrice;
	private final BigDecimal basePrice;
	private final boolean wideArea;
	private final double distanceFactor;
	private final int narrowFields;
	private final int wideFields;
	private final BigDecimal amount;
	private final BigDecimal discount;
	private final BigDecimal promotion;
	private final BigDecimal totalAmount;

	private volatile double startBatteryLevel;
	private volatile double endBatteryLevel;
	private volatile Breakdown breakdown;
	private volatile boolean generated;

	/**
	 * Creates the invoice of a rental and calculates its prices.
	 *
	 * @param rental  The rental.
	 * @param vehicle The rented vehicle.
	 * @param conf    The application configuration.
	 */
	public Invoice(Rental rental, TransportVehicle vehicle, AppConfig conf) {
		this.rental = rental;
		this.vehicle = vehicle;
		int scale = conf.getMoneyScale();

		int narrow = 0;
		for (Field field : rental.getRoute()) {
			if (conf.isNarrowArea(field))
				narrow++;
		}
		this.narrowFields = narrow;
		this.wideFields = rental.getRoute().size() - narrow;
		this.wideArea = wideFields > 0;
		this.distanceFactor = wideArea ? conf.getDistanceWide() : conf.getDistanceNarrow();

		this.unitPrice = rental.isBreakdown() ? conf.getBreakdownUnitPrice() : conf.getUnitPrice(vehicle.getType());
		this.basePrice = BigDecimal.valueOf(unitPrice).multiply(BigDecimal.valueOf(rental.getDurationInSeconds()));
		this.amount = basePrice.multiply(BigDecimal.valueOf(distanceFactor)).setScale(scale, RoundingMode.HALF_UP);
		this.discount = rental.isDiscount() ? percentOf(amount, conf.getDiscount(), scale) : zero(scale);
		this.promotion = rental.isPromotion() ? percentOf(amount, conf.getDiscountProm(), scale) : zero(scale);
		this.totalAmount = amount.subtract(discount).subtract(promotion);
		this.startBatteryLevel = vehicle.getBatteryLevel();
		this.endBatteryLevel = vehicle.getBatteryLevel();
	}

	private static BigDecimal percentOf(BigDecimal value, double rate, int scale) {
		return value.multiply(BigDecimal.valueOf(rate)).setScale(scale, RoundingMode.HALF_UP);
	}

	private static BigDecimal zero(int scale) {
		return BigDecimal.ZERO.setScale(scale);
	}

	/**
	 * Writes the invoice to a text file in the invoice folder, named after the
	 * user and the end of the ride, and marks it as generated.
	 *
	 * @param conf The application configuration.
	 * @return The path of the written file.
	 * @throws IOException If the file cannot be written.
	 */
	public Path writeToFile(AppConfig conf) throws IOException {
		Path folder = Paths.get(conf.getInvoiceFolder());
		Files.createDirectories(folder);
		String fileName = rental.getUser().getName() + " - "
				+ rental.getEndTime().format(conf.getInvoiceFileDateTimeFormatter()) + conf.getTextFileExtension();
		Path file = folder.resolve(fileName);
		Files.writeString(file, toText(conf), StandardCharsets.UTF_8);
		generated = true;
		return file;
	}

	/**
	 * Returns the text of the invoice, with every item listed separately.
	 *
	 * @param conf The application configuration.
	 * @return The invoice text.
	 */
	public String toText(AppConfig conf) {
		User user = rental.getUser();
		StringBuilder text = new StringBuilder();
		text.append(DOUBLE_LINE).append('\n');
		text.append(center(conf.getText("APP_TITLE"))).append('\n');
		text.append(center("INVOICE")).append('\n');
		text.append(DOUBLE_LINE).append('\n');
		row(text, "User", user.getName());
		if (user.hasDocuments()) {
			row(text, user.getDocumentType() == User.DocumentType.PASSPORT ? conf.getText("PASSPORT_NAME")
					: conf.getText("ID_CARD_NAME"), user.getIdentificationDocument());
			row(text, "Driver's license", user.getDriverLicenseNumber());
		}
		row(text, "Vehicle", conf.getVehicleTypeName(vehicle.getType()) + " " + vehicle.getId() + " ("
				+ vehicle.getManufacturer() + " " + vehicle.getModel() + ")");
		row(text, "Start", rental.getStartTime().format(conf.getRentalDateTimeFormatter()) + "  at "
				+ rental.getStartLocation());
		row(text, "End", rental.getEndTime().format(conf.getRentalDateTimeFormatter()) + "  at "
				+ rental.getEndLocation());
		row(text, "Duration", formatDuration(rental.getDurationInSeconds()));
		row(text, "Route", rental.getRoute().size() + " fields (" + narrowFields + " "
				+ conf.getText("NARROW_AREA_NAME").toLowerCase(Locale.ROOT) + ", " + wideFields + " "
				+ conf.getText("WIDE_AREA_NAME").toLowerCase(Locale.ROOT) + ")");
		row(text, "Battery", format("%.0f%% -> %.0f%%", startBatteryLevel, endBatteryLevel));
		text.append(LINE).append('\n');
		row(text, "Unit price", format("%.2f per second", unitPrice));
		row(text, "Base price", basePrice.setScale(amount.scale(), RoundingMode.HALF_UP).toPlainString());
		row(text, "Distance factor", format("x %.2f (%s)", distanceFactor,
				conf.getText(wideArea ? "WIDE_AREA_NAME" : "NARROW_AREA_NAME").toLowerCase(Locale.ROOT)));
		row(text, "Amount", amount.toPlainString());
		row(text, "Discount", (rental.isDiscount() ? "-" : "") + discount.toPlainString());
		row(text, "Promotion", (rental.isPromotion() ? "-" : "") + promotion.toPlainString());
		text.append(LINE).append('\n');
		row(text, "TOTAL", totalAmount.toPlainString());
		text.append(DOUBLE_LINE).append('\n');
		row(text, "Breakdown", breakdown == null ? "No" : breakdown.toString());
		return text.toString();
	}

	private static void row(StringBuilder text, String label, String value) {
		text.append(String.format(Locale.ROOT, "%-" + LABEL_WIDTH + "s%s%n", label + ":", value));
	}

	private static String center(String value) {
		int padding = Math.max(0, (DOUBLE_LINE.length() - value.length()) / 2);
		return " ".repeat(padding) + value;
	}

	private static String format(String pattern, Object... values) {
		return String.format(Locale.ROOT, pattern, values);
	}

	private static String formatDuration(long seconds) {
		return format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
	}

	public Rental getRental() {
		return rental;
	}

	public TransportVehicle getVehicle() {
		return vehicle;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @return Unit price * duration (before the distance factor).
	 */
	public BigDecimal getBasePrice() {
		return basePrice;
	}

	/**
	 * @return True if at least one field of the route is in the wide part of the
	 *         city, so the wide tariff applies.
	 */
	public boolean isWideArea() {
		return wideArea;
	}

	public double getDistanceFactor() {
		return distanceFactor;
	}

	public int getNarrowFields() {
		return narrowFields;
	}

	public int getWideFields() {
		return wideFields;
	}

	/**
	 * @return Base price * distance factor.
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public BigDecimal getPromotion() {
		return promotion;
	}

	/**
	 * @return The amount to pay: amount - discount - promotion.
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public double getStartBatteryLevel() {
		return startBatteryLevel;
	}

	public void setStartBatteryLevel(double startBatteryLevel) {
		this.startBatteryLevel = startBatteryLevel;
	}

	public double getEndBatteryLevel() {
		return endBatteryLevel;
	}

	public void setEndBatteryLevel(double endBatteryLevel) {
		this.endBatteryLevel = endBatteryLevel;
	}

	public Breakdown getBreakdown() {
		return breakdown;
	}

	public void setBreakdown(Breakdown breakdown) {
		this.breakdown = breakdown;
	}

	/**
	 * @return True once the ride has been simulated and the invoice written.
	 */
	public boolean isGenerated() {
		return generated;
	}
}
