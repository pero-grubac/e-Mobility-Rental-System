package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.etfbl.pj2.exception.DuplicateException;
import net.etfbl.pj2.exception.FieldOutOfRangeException;
import net.etfbl.pj2.exception.ParsingException;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Parses rental data from a file and constructs a list of Rental objects.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class RentalParser {
	/**
	 * Parses rental data from a file and constructs a list of Rental objects.
	 * 
	 * @param conf The application configuration.
	 * @return A list of Rental objects parsed from the file.
	 */
	public List<Rental> parseRentals(AppConfig conf) {
		List<Rental> rentals = new ArrayList<>();
		try {
			String filePath = conf.getTestFolder() + File.separator + conf.getTestRental();

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

						rentals.add(
								new Rental(username, vehicleId, start, end, startDate, duration, promotion, breakdown));

					} catch (ParsingException | FieldOutOfRangeException e) {
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
		return validateRental(
				rentals.stream().sorted(Comparator.comparing(Rental::getStartTime)).collect(Collectors.toList()));
	}

	/**
	 * Validates the format of the input line.
	 * 
	 * @param values The values parsed from the input line.
	 * @throws ParsingException if the line format is invalid.
	 */
	private void validateLineFormat(String[] values) throws ParsingException {
		if (values.length != 10) {
			throw new ParsingException("Invalid line format");
		}
	}

	/**
	 * Parses a date string into a LocalDateTime object.
	 * 
	 * @param value The date string to parse.
	 * @return The LocalDateTime object parsed from the date string.
	 * @throws ParsingException if the date string is invalid.
	 */
	private LocalDateTime parseDate(String value) throws ParsingException {
		return LocalDateTime.parse(value, Rental.DATE_TIME_FORMATTER);
	}

	/**
	 * Parses a field string into a Field object.
	 * 
	 * @param xValue The x-coordinate value of the field.
	 * @param yValue The y-coordinate value of the field.
	 * @param conf   The application configuration.
	 * @return The Field object parsed from the field string.
	 * @throws ParsingException if the field string is invalid.
	 */
	private Field parseField(String xValue, String yValue, AppConfig conf) throws ParsingException {
		try {
			Integer x = Integer.parseInt(xValue);
			Integer y = Integer.parseInt(yValue);
			return new Field(x, y);
		} catch (Exception e) {
			throw new ParsingException("Field cannot be: (" + xValue + "," + yValue + ")");
		}

	}

	/**
	 * Parses a duration string into a Long value.
	 * 
	 * @param value The duration string to parse.
	 * @return The Long value parsed from the duration string.
	 * @throws ParsingException if the duration string is invalid.
	 */
	private Long parseDuration(String value) throws ParsingException {
		Long duration = Long.parseLong(value);
		if (duration <= 0) {
			throw new ParsingException("Duration cannot be: " + duration);
		}
		return duration;
	}

	/**
	 * Validates a string representing a yes/no value.
	 * 
	 * @param input The string value to validate.
	 * @param conf  The application configuration.
	 * @return true if the string represents "yes", false if it represents "no".
	 * @throws ParsingException if the string value is neither "yes" nor "no".
	 */
	private boolean validateYesOrNo(String input, AppConfig conf) throws ParsingException {
		if (conf.getYes().equalsIgnoreCase(input)) {
			return true;
		} else if (conf.getNo().equalsIgnoreCase(input)) {
			return false;
		} else {
			throw new ParsingException("Invalid value for boolean field: " + input);
		}
	}

	/**
	 * Validates the start and end fields.
	 * 
	 * @param start The start field.
	 * @param end   The end field.
	 * @param conf  The application configuration.
	 * @throws FieldOutOfRangeException if either start or end field is out of
	 *                                  range.
	 */
	private void validateField(Field start, Field end, AppConfig conf) throws FieldOutOfRangeException {
		if (isFieldOutOfRange(start, conf) || isFieldOutOfRange(end, conf)) {
			throw new FieldOutOfRangeException(start + " " + end);
		}
	}

	/**
	 * Checks if a field is out of range.
	 * 
	 * @param field The field to check.
	 * @param conf  The application configuration.
	 * @return true if the field is out of range, false otherwise.
	 */
	private boolean isFieldOutOfRange(Field field, AppConfig conf) {
		return field.getX() < conf.getTableXMin() || field.getX() > conf.getTableXMax()
				|| field.getY() < conf.getTableYMin() || field.getY() > conf.getTableYMax();
	}

	/**
	 * Validates a list of rentals, ensuring no overlapping rentals occur for the
	 * same vehicle.
	 * 
	 * @param rentals The list of rentals to validate.
	 * @return A list of validated rentals.
	 */
	private List<Rental> validateRental(List<Rental> rentals) {
		List<Rental> newRental = new ArrayList<>();
        Map<String, LocalDateTime> lastRentalEndTimes = new HashMap<>();
        Map<String, LocalDateTime> userRentalEndTimes = new HashMap<>();
        Set<String> userVehiclePairs = new HashSet<>();

        for (Rental rental : rentals) {
            try {
                LocalDateTime lastEndTimeForVehicle = lastRentalEndTimes.get(rental.getVehicleId());
                LocalDateTime lastEndTimeForUser = userRentalEndTimes.get(rental.getUser().getName());

                // Provjera da li je vozilo već iznajmljeno u isto vrijeme
                if (lastEndTimeForVehicle != null && lastEndTimeForVehicle.isAfter(rental.getStartTime())) {
                    throw new ParsingException("Vehicle " + rental.getVehicleId() + " is already rented.");
                }

                // Provjera da li korisnik već iznajmljuje drugo vozilo u isto vrijeme
                if (lastEndTimeForUser != null && lastEndTimeForUser.isAfter(rental.getStartTime())) {
                    throw new ParsingException("User " + rental.getUser().getName() + " is already renting another vehicle.");
                }

                // Provjera da li korisnik već iznajmljuje isto vozilo u isto vrijeme
                String userVehicleKey = rental.getUser().getName() + "-" + rental.getVehicleId();
                if (userVehiclePairs.contains(userVehicleKey)) {
                    throw new ParsingException("User " + rental.getUser().getName() + " is already renting vehicle " + rental.getVehicleId() + " at the same time.");
                }

                newRental.add(rental);
                lastRentalEndTimes.put(rental.getVehicleId(), rental.getStartTime().plusSeconds(rental.getDurationInSeconds()));
                userRentalEndTimes.put(rental.getUser().getName(), rental.getStartTime().plusSeconds(rental.getDurationInSeconds()));
                userVehiclePairs.add(userVehicleKey);
            } catch (ParsingException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        return newRental;

	}
}
