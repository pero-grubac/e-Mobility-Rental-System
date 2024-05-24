package net.etfbl.pj2.breakdown;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

public class Breakdown {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

	private LocalDateTime breakdownDateTime;
	private String description;

	public Breakdown(LocalDateTime breakdownDateTime) {
		this.breakdownDateTime = breakdownDateTime;
		AppConfig conf = new AppConfig();
		this.description = Util.generateUUID(conf.getBreakdownReasonLength());
	}

	@Override
	public String toString() {
		return "Breakdown: [breakdownDateTime=" + breakdownDateTime.format(DATE_TIME_FORMATTER) + ", description="
				+ description + "]";
	}

}
