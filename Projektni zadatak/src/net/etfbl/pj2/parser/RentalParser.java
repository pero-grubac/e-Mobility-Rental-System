package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.etfb.pj2.exception.DuplicateException;
import net.etfb.pj2.exception.FieldOutOfRangeException;
import net.etfb.pj2.exception.ParsingException;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;

public class RentalParser {

	public List<Rental> parseRentals(String filePath) {
		List<Rental> rentals = new ArrayList<>();
		AppConfig conf = new AppConfig();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
			br.readLine(); // Preskočiti zaglavlje

			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(",");
					validateLineFormat(values);

					LocalDateTime startDate = parseDate(values[0].replace("\"", ""));
					String username = values[1];
					String vehicleId = values[2];

					Field start = parseField(values[3].replace("\"", ""), values[4].replace("\"", ""), conf);
					Field end = parseField(values[5].replace("\"", ""), values[6].replace("\"", ""), conf);

					validateField(start, end, conf);

					Long duration = parseDuration(values[7]);
					boolean breakdown = validateYesOrNo(values[8], conf);
					boolean promotion = validateYesOrNo(values[9].replace("\"", ""), conf);

					rentals.add(new Rental(username, vehicleId, start, end, startDate, duration, promotion, breakdown));

				} catch (ParsingException | FieldOutOfRangeException e) {
					System.err.println(e.getMessage());
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		return validateRental(
				rentals.stream().sorted(Comparator.comparing(Rental::getStartTime)).collect(Collectors.toList()));
	}

	private void validateLineFormat(String[] values) throws ParsingException {
		if (values.length != 10) {
			throw new ParsingException("Invalid line format");
		}
	}

	private LocalDateTime parseDate(String value) throws ParsingException {
		return LocalDateTime.parse(value, Rental.DATE_TIME_FORMATTER);
	}

	private Field parseField(String xValue, String yValue, AppConfig conf) throws ParsingException {
		try {
			Integer x = Integer.parseInt(xValue);
			Integer y = Integer.parseInt(yValue);
			return new Field(x, y);
		} catch (Exception e) {
			throw new ParsingException("Field cannot be: (" + xValue + "," + yValue+")");
		}

	}

	private Long parseDuration(String value) throws ParsingException {
		Long duration = Long.parseLong(value);
		if (duration <= 0) {
			throw new ParsingException("Duration cannot be: " + duration);
		}
		return duration;
	}

	private boolean validateYesOrNo(String input, AppConfig conf) throws ParsingException {
		if (conf.getYes().equalsIgnoreCase(input)) {
			return true;
		} else if (conf.getNo().equalsIgnoreCase(input)) {
			return false;
		} else {
			throw new ParsingException("Invalid value for boolean field: " + input);
		}
	}

	private void validateField(Field start, Field end, AppConfig conf) throws FieldOutOfRangeException {
		if (isFieldOutOfRange(start, conf) || isFieldOutOfRange(end, conf)) {
			throw new FieldOutOfRangeException(start + " " + end);
		}
	}

	private boolean isFieldOutOfRange(Field field, AppConfig conf) {
		return field.getX() < conf.getTableXMin() || field.getX() > conf.getTableXMax()
				|| field.getY() < conf.getTableYMin() || field.getY() > conf.getTableYMax();
	}

	private List<Rental> validateRental(List<Rental> rentals) {
		List<Rental> newRental = new ArrayList<>();
		Map<String, LocalDateTime> lastRentalEndTimes = new HashMap<>();

		for (Rental rental : rentals) {
			try {
				LocalDateTime lastEndTime = lastRentalEndTimes.get(rental.getVehicleId());
				if (lastEndTime != null && lastEndTime.isAfter(rental.getStartTime())) {
					throw new ParsingException("Vehicle " + rental.getVehicleId() + " is already rented.");
				} else {
					newRental.add(rental);
					lastRentalEndTimes.put(rental.getVehicleId(),
							rental.getStartTime().plusSeconds(rental.getDurationInSeconds()));
				}
			} catch (ParsingException e) {
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}

		return newRental;

	}
}
