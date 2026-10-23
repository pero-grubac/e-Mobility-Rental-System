package net.etfbl.pj2.breakdown;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A breakdown of a vehicle: when it happened and why.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Breakdown implements Serializable {
	private static final long serialVersionUID = 2L;

	/** Format of the breakdown date and time. */
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

	private final LocalDateTime dateTime;
	private final String description;

	/**
	 * Constructs a new breakdown.
	 *
	 * @param dateTime    The date and time of the breakdown.
	 * @param description The reason of the breakdown.
	 */
	public Breakdown(LocalDateTime dateTime, String description) {
		this.dateTime = dateTime;
		this.description = description;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return dateTime.format(DATE_TIME_FORMATTER) + " - " + description;
	}
}
