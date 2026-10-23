package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.etfbl.pj2.exception.DuplicateException;
import net.etfbl.pj2.exception.ParsingException;
import net.etfbl.pj2.exception.UnknownVehicleException;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Reads the vehicles CSV file. Columns: ID, manufacturer, model, purchase date,
 * purchase price, range, max speed, description, type. Invalid lines are
 * reported and skipped.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class VehicleParser {
	private static final int COLUMNS = 9;

	/**
	 * Parses all valid vehicles from the file set in the configuration.
	 *
	 * @param conf The application configuration.
	 * @return The vehicles in file order; empty if the file cannot be read.
	 */
	public List<TransportVehicle> parseVehicles(AppConfig conf) {
		List<TransportVehicle> vehicles = new ArrayList<>();
		Set<String> ids = new HashSet<>();
		try (BufferedReader reader = Files.newBufferedReader(conf.getVehiclesFile(), StandardCharsets.UTF_8)) {
			reader.readLine(); // header
			String line;
			int lineNumber = 1;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				if (line.isBlank())
					continue;
				try {
					TransportVehicle vehicle = parseLine(line, conf);
					if (!ids.add(vehicle.getId())) {
						throw new DuplicateException("Duplicate vehicle ID found: " + vehicle.getId());
					}
					vehicles.add(vehicle);
				} catch (ParsingException | DuplicateException | UnknownVehicleException e) {
					System.err.println("Vehicles, line " + lineNumber + ": " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Vehicles file cannot be read: " + e.getMessage());
		}
		return vehicles;
	}

	/**
	 * Parses one line of the vehicles file.
	 *
	 * @param line The line.
	 * @param conf The application configuration.
	 * @return The vehicle.
	 * @throws ParsingException        If a value is missing or invalid.
	 * @throws UnknownVehicleException If the type is not a known vehicle type.
	 */
	private TransportVehicle parseLine(String line, AppConfig conf) throws ParsingException, UnknownVehicleException {
		String[] values = line.split(",", -1);
		if (values.length != COLUMNS) {
			throw new ParsingException("Expected " + COLUMNS + " columns but found " + values.length);
		}
		String id = requireText(values[0], "ID");
		String manufacturer = requireText(values[1], "manufacturer");
		String model = requireText(values[2], "model");
		double price = parseNonNegativeNumber(values[4], "purchase price");
		String description = values[7].trim();
		VehicleType type = parseType(requireText(values[8], "type"), conf);

		switch (type) {
		case CAR:
			return new Car(id, manufacturer, model, price, parseDate(values[3], conf), description);
		case BIKE:
			return new ElectricBike(id, manufacturer, model, price, parseOptionalInt(values[5], "range"));
		default:
			return new ElectricScooter(id, manufacturer, model, price, parseOptionalInt(values[6], "max speed"));
		}
	}

	private VehicleType parseType(String value, AppConfig conf) throws UnknownVehicleException {
		for (VehicleType type : VehicleType.values()) {
			if (conf.getCsvVehicleType(type).equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new UnknownVehicleException(value);
	}

	private String requireText(String value, String fieldName) throws ParsingException {
		if (value.isBlank()) {
			throw new ParsingException(fieldName + " cannot be empty");
		}
		return value.trim();
	}

	private double parseNonNegativeNumber(String value, String fieldName) throws ParsingException {
		try {
			double number = Double.parseDouble(value.trim());
			if (number < 0) {
				throw new ParsingException(fieldName + " cannot be negative");
			}
			return number;
		} catch (NumberFormatException e) {
			throw new ParsingException(fieldName + " must be a valid number");
		}
	}

	private int parseOptionalInt(String value, String fieldName) throws ParsingException {
		if (value.isBlank())
			return 0;
		try {
			int number = Integer.parseInt(value.trim());
			if (number < 0) {
				throw new ParsingException(fieldName + " cannot be negative");
			}
			return number;
		} catch (NumberFormatException e) {
			throw new ParsingException(fieldName + " must be a whole number");
		}
	}

	private LocalDate parseDate(String value, AppConfig conf) throws ParsingException {
		if (value.isBlank())
			return null;
		try {
			return LocalDate.parse(value.trim(), conf.getPurchaseDateFormatter());
		} catch (DateTimeParseException e) {
			throw new ParsingException("invalid purchase date: " + value);
		}
	}
}
