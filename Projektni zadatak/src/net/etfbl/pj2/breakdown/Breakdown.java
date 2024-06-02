package net.etfbl.pj2.breakdown;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

/**
 * Represents a breakdown with a specific date and time, and a generated
 * description. The description is generated based on a configuration value.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Breakdown {
	/**
	 * Formatter for the date and time of the breakdown.
	 */
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

	private LocalDateTime breakdownDateTime;
	private String description;

	/**
	 * Constructs a new Breakdown with the specified date and time. The description
	 * is generated based on a configuration value.
	 * 
	 * @param breakdownDateTime The date and time of the breakdown.
	 */
	public Breakdown(LocalDateTime breakdownDateTime) {
		this.breakdownDateTime = breakdownDateTime;
		AppConfig conf = new AppConfig();
		try {
			this.description = Util.generateUUID(conf.getBreakdownReasonLength());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Returns a string representation of the Breakdown.
	 * 
	 * @return A string representation of the Breakdown.
	 */
	@Override
	public String toString() {
		return "Breakdown: [breakdownDateTime=" + breakdownDateTime.format(DATE_TIME_FORMATTER) + ", description="
				+ description + "]";
	}

}
