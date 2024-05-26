package net.etfbl.pj2.parser;

import java.io.BufferedReader;
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

import net.etfb.pj2.exception.DuplicateException;
import net.etfb.pj2.exception.ParsingException;
import net.etfb.pj2.exception.UnknownVehicleException;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

public class VehicleParser {

	public List<TransportVehicle> parseVehicles(String filePath) {
		Set<TransportVehicle> vehicles = new LinkedHashSet<>();
		Set<String> vehicleIds = new HashSet<>();

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
							: LocalDate.parse(values[3], TransportVehicle.DATE_FORMATTER);
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

		return new ArrayList<>(vehicles);
	}

	private void validateLineFormat(String[] values) throws ParsingException {
		if (values.length != 9) {
			throw new ParsingException("Invalid line format");
		}
	}

	private String validateAndRetrieveId(String id, Set<String> vehicleIds) throws DuplicateException {
		if (vehicleIds.contains(id)) {
			throw new DuplicateException("Duplicate vehicle ID found: " + id);
		}
		vehicleIds.add(id);
		return id;
	}

	private String validateAndRetrieveString(String value, String fieldName) throws ParsingException {
		if (value.isEmpty() || value.isBlank()) {
			throw new ParsingException(fieldName + " cannot be empty");
		}
		return value;
	}

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
}
