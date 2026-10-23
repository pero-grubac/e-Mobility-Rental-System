package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.etfbl.pj2.exception.FieldOutOfRangeException;
import net.etfbl.pj2.exception.ParsingException;
import net.etfbl.pj2.exception.UnknownVehicleException;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Reads the rentals CSV file. Columns: date and time, user, vehicle ID, start
 * location "x,y", end location "x,y", duration in seconds, breakdown (yes/no),
 * promotion (yes/no). Invalid lines are reported and skipped.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class RentalParser {
	/**
	 * Locations are written as "x,y", with or without quotes, so after removing
	 * the quotes a line has 10 comma separated values.
	 */
	private static final int VALUES = 10;

	/**
	 * Parses all valid rentals, sorted by start time.
	 *
	 * @param conf The application configuration.
	 * @return The rentals.
	 */
	public List<Rental> parseRentals(AppConfig conf) {
		return parseRentals(conf, null);
	}

	/**
	 * Parses all valid rentals of known vehicles, sorted by start time. A vehicle
	 * cannot be rented twice at the same time, and a user cannot rent two vehicles
	 * at the same time; the later of two overlapping rentals is skipped.
	 *
	 * @param conf       The application configuration.
	 * @param vehicleIds The IDs of existing vehicles, or null to accept any ID.
	 * @return The rentals.
	 */
	public List<Rental> parseRentals(AppConfig conf, Set<String> vehicleIds) {
		List<Rental> rentals = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(conf.getRentalsFile(), StandardCharsets.UTF_8)) {
			reader.readLine(); // header
			String line;
			int lineNumber = 1;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				if (line.isBlank())
					continue;
				try {
					Rental rental = parseLine(line, conf);
					if (vehicleIds != null && !vehicleIds.contains(rental.getVehicleId())) {
						throw new UnknownVehicleException(rental.getVehicleId());
					}
					rentals.add(rental);
				} catch (ParsingException | FieldOutOfRangeException | UnknownVehicleException e) {
					System.err.println("Rentals, line " + lineNumber + ": " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Rentals file cannot be read: " + e.getMessage());
		}
		rentals.sort(Comparator.comparing(Rental::getStartTime));
		return removeOverlapping(rentals);
	}

	/**
	 * Parses one line of the rentals file.
	 *
	 * @param line The line.
	 * @param conf The application configuration.
	 * @return The rental.
	 * @throws ParsingException         If a value is missing or invalid.
	 * @throws FieldOutOfRangeException If a location is outside the map.
	 */
	private Rental parseLine(String line, AppConfig conf) throws ParsingException, FieldOutOfRangeException {
		String[] values = line.replace("\"", "").split(",", -1);
		if (values.length != VALUES) {
			throw new ParsingException("Expected " + VALUES + " values but found " + values.length);
		}
		LocalDateTime start = parseDateTime(values[0], conf);
		String user = values[1].trim();
		String vehicleId = values[2].trim();
		if (user.isEmpty() || vehicleId.isEmpty()) {
			throw new ParsingException("user and vehicle ID cannot be empty");
		}
		Field from = parseField(values[3], values[4], conf);
		Field to = parseField(values[5], values[6], conf);
		long duration = parseDuration(values[7]);
		boolean breakdown = parseYesNo(values[8], conf);
		boolean promotion = parseYesNo(values[9], conf);
		return new Rental(user, vehicleId, from, to, start, duration, promotion, breakdown);
	}

	private LocalDateTime parseDateTime(String value, AppConfig conf) throws ParsingException {
		try {
			return LocalDateTime.parse(value.trim(), conf.getRentalDateTimeFormatter());
		} catch (DateTimeParseException e) {
			throw new ParsingException("invalid date and time: " + value);
		}
	}

	private Field parseField(String x, String y, AppConfig conf) throws ParsingException, FieldOutOfRangeException {
		Field field;
		try {
			field = new Field(Integer.parseInt(x.trim()), Integer.parseInt(y.trim()));
		} catch (NumberFormatException e) {
			throw new ParsingException("invalid field: (" + x + "," + y + ")");
		}
		if (!conf.isOnMap(field)) {
			throw new FieldOutOfRangeException(field + " is outside the map");
		}
		return field;
	}

	private long parseDuration(String value) throws ParsingException {
		try {
			long duration = Long.parseLong(value.trim());
			if (duration <= 0) {
				throw new ParsingException("duration must be positive: " + duration);
			}
			return duration;
		} catch (NumberFormatException e) {
			throw new ParsingException("invalid duration: " + value);
		}
	}

	private boolean parseYesNo(String value, AppConfig conf) throws ParsingException {
		if (conf.getYes().equalsIgnoreCase(value.trim()))
			return true;
		if (conf.getNo().equalsIgnoreCase(value.trim()))
			return false;
		throw new ParsingException("expected " + conf.getYes() + " or " + conf.getNo() + " but found: " + value);
	}

	/**
	 * Skips rentals that overlap with an earlier rental of the same vehicle or of
	 * the same user.
	 *
	 * @param rentals The rentals, sorted by start time.
	 * @return The rentals without overlaps.
	 */
	private List<Rental> removeOverlapping(List<Rental> rentals) {
		List<Rental> valid = new ArrayList<>();
		Map<String, LocalDateTime> vehicleFreeAt = new HashMap<>();
		Map<String, LocalDateTime> userFreeAt = new HashMap<>();

		for (Rental rental : rentals) {
			LocalDateTime vehicleFree = vehicleFreeAt.get(rental.getVehicleId());
			LocalDateTime userFree = userFreeAt.get(rental.getUser().getName());
			if (vehicleFree != null && vehicleFree.isAfter(rental.getStartTime())) {
				System.err.println("Rental skipped: vehicle " + rental.getVehicleId() + " is already rented ("
						+ rental + ")");
			} else if (userFree != null && userFree.isAfter(rental.getStartTime())) {
				System.err.println("Rental skipped: user " + rental.getUser().getName()
						+ " is already renting another vehicle (" + rental + ")");
			} else {
				valid.add(rental);
				vehicleFreeAt.put(rental.getVehicleId(), rental.getEndTime());
				userFreeAt.put(rental.getUser().getName(), rental.getEndTime());
			}
		}
		return valid;
	}
}
