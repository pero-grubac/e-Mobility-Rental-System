package net.etfbl.pj2.rental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.etfbl.pj2.model.Field;

public class Rental {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

	private String rentalId;
	private String username;
	private String vehicleId;
	private String userName;
	private Field startLocation;
	private Field endLocation;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Integer durationInSeconds;
	private BigDecimal price;
	private Boolean isWideArea;
	private Boolean isPromotion;
	private Boolean isDiscount;
	private Boolean isBreakdown;

	public Rental(String userId, String vehicleId, Field startLocation, Field endLocation, LocalDateTime startTime,
			Integer durationInSeconds, Boolean isPromotion, Boolean isBreakdown) {
		super();
		this.username = userId;
		this.vehicleId = vehicleId;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.startTime = startTime;
		this.durationInSeconds = durationInSeconds;
		this.isPromotion = isPromotion;
		this.isBreakdown = isBreakdown;
	}

	@Override
	public String toString() {
		return "Rental rentalId=" + rentalId + ", username=" + username + ", vehicleId=" + vehicleId + ", userName="
				+ userName + ", startLocation=" + startLocation + ", endLocation=" + endLocation + ", startTime="
				+ (startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "N/A") + ", endTime="
				+ (endTime != null ? endTime.format(DATE_TIME_FORMATTER) : "N/A") + ", durationInSeconds="
				+ durationInSeconds + ", price=" + price + ", isWideArea=" + isWideArea + ", isPromotion=" + isPromotion
				+ ", isDiscount=" + isDiscount + ", isBreakdown=" + isBreakdown;
	}

}
