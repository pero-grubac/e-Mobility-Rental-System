package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.etfbl.pj2.exception.DuplicateException;
import net.etfbl.pj2.exception.ParsingException;
import net.etfbl.pj2.exception.UnknownVehicleException;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Parses vehicles from a text file.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class VehicleParser {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy.");

	/**
	 * Parses the vehicles from the specified file using the given configuration.
	 *
	 * @param conf The application configuration.
	 * @return A list of parsed transport vehicles.
	 */
	public List<TransportVehicle> parseVehicles(AppConfig conf) {
		Set<TransportVehicle> vehicles = new LinkedHashSet<>();
		Set<String> vehicleIds = new HashSet<>();
		try {
			String filePath = conf.getTestFolder() + File.separator + conf.getTestVehicle();

			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				String line;
				br.readLine(); // Preskočiti zaglavlje

				while ((line = br.readLine()) != null) {
					try {
						String[] values = line.split(",");

						validateLineFormat(values);

						String id = validateAndRetrieveId(values[0], vehicleIds);
						String manufacturer = validateAndRetrieveString(values[1], "manufacturer");
						String model = validateAndRetrieveString(values[2], "model");
						LocalDate purchaseDate = values[3].isEmpty() ? null
								: LocalDate.parse(values[3], TransportVehicle.getDateFormatter());
						Double purchasePrice = validateAndRetrieveDouble(values[4], "purchase price");
						Integer range = values[5].isEmpty() ? 0 : Integer.parseInt(values[5]);
						Integer maxSpeed = values[6].isEmpty() ? 0 : Integer.parseInt(values[6]);
						String description = values[7];
						String type = validateAndRetrieveString(values[8], "type");

						TransportVehicle vehicle = createVehicle(id, manufacturer, model, purchasePrice, purchaseDate,
								range, maxSpeed, description, type);
						vehicles.add(vehicle);
					} catch (ParsingException | DuplicateException | UnknownVehicleException e) {
						System.err.println(e.getMessage());
					} catch (Exception e) {
						System.err.println("Error: " + e.getMessage());
					}
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());

		}
		return new ArrayList<>(vehicles);
	}

	/**
	 * Validates the format of a line containing vehicle information.
	 *
	 * @param values The values parsed from the line.
	 * @throws ParsingException If the line format is invalid.
	 */
	private void validateLineFormat(String[] values) throws ParsingException {
		if (values.length != 9) {
			throw new ParsingException("Invalid line format");
		}
	}

	/**
	 * Validates and retrieves the ID of a vehicle from the given set of IDs.
	 *
	 * @param id         The ID of the vehicle.
	 * @param vehicleIds The set of existing vehicle IDs.
	 * @return The validated vehicle ID.
	 * @throws DuplicateException If the ID is already present in the set.
	 */
	private String validateAndRetrieveId(String id, Set<String> vehicleIds) throws DuplicateException {
		if (vehicleIds.contains(id)) {
			throw new DuplicateException("Duplicate vehicle ID found: " + id);
		}
		vehicleIds.add(id);
		return id;
	}

	/**
	 * Validates and retrieves a string value.
	 *
	 * @param value     The string value to validate.
	 * @param fieldName The name of the field associated with the value.
	 * @return The validated string value.
	 * @throws ParsingException If the value is empty or blank.
	 */
	private String validateAndRetrieveString(String value, String fieldName) throws ParsingException {
		if (value.isEmpty() || value.isBlank()) {
			throw new ParsingException(fieldName + " cannot be empty");
		}
		return value;
	}

	/**
	 * Validates and retrieves a double value.
	 *
	 * @param value     The double value to validate.
	 * @param fieldName The name of the field associated with the value.
	 * @return The validated double value.
	 * @throws ParsingException If the value is not a valid number or is negative.
	 */
	private Double validateAndRetrieveDouble(String value, String fieldName) throws ParsingException {
		try {
			Double doubleValue = Double.parseDouble(value);
			if (doubleValue < 0) {
				throw new ParsingException(fieldName + " cannot be negative");
			}
			return doubleValue;
		} catch (NumberFormatException e) {
			throw new ParsingException(fieldName + " must be a valid number");
		}
	}

	/**
	 * Creates a transport vehicle based on the provided parameters.
	 *
	 * @param id            The ID of the vehicle.
	 * @param manufacturer  The manufacturer of the vehicle.
	 * @param model         The model of the vehicle.
	 * @param purchasePrice The purchase price of the vehicle.
	 * @param purchaseDate  The purchase date of the vehicle.
	 * @param range         The range of the vehicle.
	 * @param maxSpeed      The maximum speed of the vehicle.
	 * @param description   The description of the vehicle.
	 * @param type          The type of the vehicle.
	 * @return A new instance of the transport vehicle.
	 * @throws UnknownVehicleException If the vehicle type is unknown.
	 */
	private TransportVehicle createVehicle(String id, String manufacturer, String model, Double purchasePrice,
			LocalDate purchaseDate, Integer range, Integer maxSpeed, String description, String type)
			throws UnknownVehicleException {
		switch (type.toLowerCase()) {
		case "automobil":
			return new Car(id, manufacturer, model, purchasePrice, purchaseDate, description);
		case "bicikl":
			return new ElectricBike(id, manufacturer, model, purchasePrice, range);
		case "trotinet":
			return new ElectricScooter(id, manufacturer, model, purchasePrice, maxSpeed);
		default:
			throw new UnknownVehicleException(type);
		}
	}

	/**
	 * Parses an electric scooter from the given line of text.
	 *
	 * @param line The line containing the electric scooter information.
	 * @return The parsed electric scooter.
	 * @throws ParsingException If the line format is invalid or contains incorrect
	 *                          data.
	 */
	public ElectricScooter parseElectricScooter(String line) throws ParsingException {
		ElectricScooter scooter = new ElectricScooter();
		String[] parts = line.split(",\\s+");
		if (parts.length != 6) {
			throw new ParsingException("Invalid number of parameters for ElectricScooter");
		}
		scooter.setId(getValue(parts[0]));
		scooter.setManufacturer(getValue(parts[1]));
		scooter.setModel(getValue(parts[2]));
		scooter.setPurchasePrice(validateAndRetrieveDouble(getValue(parts[3]), "purchase price"));
		scooter.setBatteryLevel(parseBatteryLevel(getValue(parts[4])));
		scooter.setMaxSpeed(parsePositiveInteger(getValue(parts[5]), "max speed"));
		return scooter;
	}

	/**
	 * Parses an electric bike from the given line of text.
	 *
	 * @param line The line containing the electric bike information.
	 * @return The parsed electric bike.
	 * @throws ParsingException If the line format is invalid or contains incorrect
	 *                          data.
	 */
	public ElectricBike parseElectricBike(String line) throws ParsingException {
		ElectricBike bike = new ElectricBike();
		String[] parts = line.split(",\\s+");
		if (parts.length != 6) {
			throw new ParsingException("Invalid number of parameters for ElectricBike");
		}
		bike.setId(getValue(parts[0]));
		bike.setManufacturer(getValue(parts[1]));
		bike.setModel(getValue(parts[2]));
		bike.setPurchasePrice(validateAndRetrieveDouble(getValue(parts[3]), "purchase price"));
		bike.setBatteryLevel(parseBatteryLevel(getValue(parts[4])));
		bike.setRangePerCharge(parsePositiveInteger(getValue(parts[5]), "range per charge"));
		return bike;
	}

	/**
	 * Parses a car from the given line of text.
	 *
	 * @param line The line containing the car information.
	 * @return The parsed car.
	 * @throws ParsingException If the line format is invalid or contains incorrect
	 *                          data.
	 */
	public Car parseCar(String line) throws ParsingException {
		Car car = new Car();
		String[] parts = line.split(",\\s+");
		if (parts.length != 7) {
			throw new ParsingException("Invalid number of parameters for Car");
		}
		car.setId(getValue(parts[0]));
		car.setManufacturer(getValue(parts[1]));
		car.setModel(getValue(parts[2]));
		car.setPurchasePrice(validateAndRetrieveDouble(getValue(parts[3]), "purchase price"));
		car.setBatteryLevel(parseBatteryLevel(getValue(parts[4])));
		car.setPurchaseDate(parseDate(getValue(parts[5]), "purchase date"));
		car.setDescription(getValue(parts[6]));
		return car;
	}

	/**
	 * Parses a generic transport vehicle from the given line of text.
	 *
	 * @param line The line containing the vehicle information.
	 * @return The parsed transport vehicle.
	 * @throws ParsingException If the line format is invalid or contains incorrect
	 *                          data.
	 */
	public TransportVehicle parseVehicle(String line) throws ParsingException {
		TransportVehicle vehicle = null;
		if (line.startsWith("ElectricScooter")) {
			vehicle = parseElectricScooter(line);
		} else if (line.startsWith("ElectricBike")) {
			vehicle = parseElectricBike(line);
		} else if (line.startsWith("Car")) {
			vehicle = parseCar(line);
		}
		if (vehicle == null) {
			throw new ParsingException("Failed to parse vehicle line: " + line);
		}
		return vehicle;
	}

	/**
	 * Parses the date from the given value with the specified field name.
	 *
	 * @param value     The string value representing the date.
	 * @param fieldName The name of the field associated with the date.
	 * @return The parsed date.
	 * @throws ParsingException If the date format is invalid or the value is empty.
	 */
	private LocalDate parseDate(String value, String fieldName) throws ParsingException {
		if (value.isEmpty()) {
			throw new ParsingException(fieldName + " cannot be empty");
		}
		try {
			return LocalDate.parse(value, DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			throw new ParsingException(fieldName + " must be in format d.M.yyyy");
		}
	}

	/**
	 * Parses a positive integer from the given value with the specified field name.
	 *
	 * @param value     The string value representing the integer.
	 * @param fieldName The name of the field associated with the integer.
	 * @return The parsed integer.
	 * @throws ParsingException If the value is not a valid positive integer.
	 */
	private Integer parsePositiveInteger(String value, String fieldName) throws ParsingException {
		try {
			Integer intValue = Integer.parseInt(value);
			if (intValue < 0) {
				throw new ParsingException(fieldName + " cannot be negative");
			}
			return intValue;
		} catch (NumberFormatException e) {
			throw new ParsingException(fieldName + " must be a valid integer");
		}
	}

	/**
	 * Parses the battery level from the given string value.
	 *
	 * @param value The string value representing the battery level.
	 * @return The parsed battery level.
	 * @throws ParsingException If the value is not a valid number.
	 */
	private Double parseBatteryLevel(String value) throws ParsingException {
		try {
			return Double.parseDouble(value.replace("%", "").replace(",00", ""));
		} catch (NumberFormatException e) {
			throw new ParsingException("Battery level must be a valid number");
		}
	}

	/**
	 * Extracts the value from the provided string part.
	 *
	 * @param part The string part containing the value.
	 * @return The extracted value.
	 */
	private String getValue(String part) {
		return part.split("=")[1].trim();
	}
}
