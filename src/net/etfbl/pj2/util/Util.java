package net.etfbl.pj2.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Helper methods shared by the rest of the application.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public final class Util {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final Random RANDOM = new Random();

	private Util() {
	}

	/**
	 * Generates a random ID of letters and digits.
	 *
	 * @param length The length of the ID.
	 * @return The ID.
	 * @throws IllegalArgumentException If the length is not positive.
	 */
	public static String generateId(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("Length must be greater than 0.");
		}
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

	/**
	 * Picks a random element of a list.
	 *
	 * @param <T>    The element type.
	 * @param values A list that is not empty.
	 * @return One of the elements.
	 */
	public static <T> T randomElement(List<T> values) {
		return values.get(RANDOM.nextInt(values.size()));
	}

	/**
	 * Calculates the route between two fields. Routes are made of straight lines
	 * only: the vehicle first drives horizontally to the target column, then
	 * vertically to the target field, one field at a time. Both ends are part of
	 * the route.
	 *
	 * @param start The starting field.
	 * @param end   The target field.
	 * @return The fields of the route, from start to end.
	 */
	public static List<Field> calculateRoute(Field start, Field end) {
		List<Field> route = new ArrayList<>();
		int x = start.getX();
		int y = start.getY();
		route.add(start);
		while (x != end.getX()) {
			x += Integer.signum(end.getX() - x);
			route.add(new Field(x, y));
		}
		while (y != end.getY()) {
			y += Integer.signum(end.getY() - y);
			route.add(new Field(x, y));
		}
		return route;
	}

	/**
	 * Gives every N-th rental the discount, counting rentals in the order they are
	 * listed (which must be chronological).
	 *
	 * @param rentals The rentals, sorted by start time.
	 * @param every   N; for example 10 means the 10th, 20th, ... rental.
	 */
	public static void applyEveryNthDiscount(List<Rental> rentals, int every) {
		for (int i = 0; i < rentals.size(); i++) {
			rentals.get(i).setDiscount((i + 1) % every == 0);
		}
	}

	/**
	 * Issues identification documents and driver's licenses to everyone who
	 * rents a car.
	 *
	 * @param rentals  The rentals.
	 * @param vehicles The vehicles.
	 * @param conf     The application configuration.
	 */
	public static void issueDocumentsToCarUsers(List<Rental> rentals, List<TransportVehicle> vehicles,
			AppConfig conf) {
		Map<String, TransportVehicle> byId = mapById(vehicles);
		rentals.stream().filter(r -> byId.containsKey(r.getVehicleId()))
				.filter(r -> byId.get(r.getVehicleId()).getType() == VehicleType.CAR)
				.forEach(r -> r.getUser().issueDocuments(conf));
	}

	/**
	 * Creates an invoice for every rental whose vehicle exists.
	 *
	 * @param rentals  The rentals.
	 * @param vehicles The vehicles.
	 * @param conf     The application configuration.
	 * @return The invoices, in the order of the rentals.
	 */
	public static List<Invoice> createInvoices(List<Rental> rentals, List<TransportVehicle> vehicles,
			AppConfig conf) {
		Map<String, TransportVehicle> byId = mapById(vehicles);
		return rentals.stream().filter(r -> byId.containsKey(r.getVehicleId()))
				.map(r -> new Invoice(r, byId.get(r.getVehicleId()), conf)).collect(Collectors.toList());
	}

	/**
	 * @param vehicles The vehicles.
	 * @return The vehicles by ID, keeping the first one of duplicate IDs.
	 */
	public static Map<String, TransportVehicle> mapById(List<TransportVehicle> vehicles) {
		return vehicles.stream().collect(Collectors.toMap(TransportVehicle::getId, Function.identity(), (a, b) -> a));
	}

	/**
	 * Groups invoices by the date of their rental, in chronological order.
	 *
	 * @param invoices The invoices.
	 * @return The invoices by date.
	 */
	public static Map<LocalDate, List<Invoice>> groupByDate(List<Invoice> invoices) {
		return invoices.stream().collect(Collectors.groupingBy(i -> i.getRental().getStartTime().toLocalDate(),
				TreeMap::new, Collectors.toList()));
	}

	/**
	 * Groups invoices by the start date and time of their rental, in
	 * chronological order.
	 *
	 * @param invoices The invoices.
	 * @return The invoices by start date and time.
	 */
	public static Map<LocalDateTime, List<Invoice>> groupByStartTime(List<Invoice> invoices) {
		return invoices.stream().collect(
				Collectors.groupingBy(i -> i.getRental().getStartTime(), TreeMap::new, Collectors.toList()));
	}
}
