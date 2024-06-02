package net.etfbl.pj2.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import net.etfbl.pj2.util.Util;

/**
 * Represents a rental transaction. Each rental has a user, vehicle ID, start
 * and end location, start and end time, duration in seconds, promotion status,
 * discount status, and breakdown status.
 * 
 *  @author Pero Grubač
 * @since 2.6.2024.
 */
public class Rental {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private User user;
	private String vehicleId;
	private Field startLocation;
	private Field endLocation;
	private transient List<Field> shortestPath;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Long durationInSeconds;
	// private Boolean isWideArea;
	private Boolean isPromotion;
	private Boolean isDiscount;
	private static transient Integer discount = 0;
	private Boolean isBreakdown;

	/**
	 * Constructs a new Rental object with the specified parameters.
	 * 
	 * @param username          The username of the user renting the vehicle.
	 * @param vehicleId         The ID of the rented vehicle.
	 * @param startLocation     The start location of the rental.
	 * @param endLocation       The end location of the rental.
	 * @param startTime         The start time of the rental.
	 * @param durationInSeconds The duration of the rental in seconds.
	 * @param isPromotion       The promotion status of the rental.
	 * @param isBreakdown       The breakdown status of the rental.
	 */
	public Rental(String username, String vehicleId, Field startLocation, Field endLocation, LocalDateTime startTime,
			Long durationInSeconds, Boolean isPromotion, Boolean isBreakdown) {
		super();
		this.user = new User(username);
		this.vehicleId = vehicleId;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.startTime = startTime;
		this.durationInSeconds = durationInSeconds;
		this.isPromotion = isPromotion;
		this.isBreakdown = isBreakdown;

		discount++;
		this.isDiscount = discount % 10 == 0;
		shortestPath = Util.calculateShortesPath(startLocation, endLocation);
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public Boolean getIsBreakdown() {
		return isBreakdown;
	}

	public void setIsBreakdown(Boolean isBreakdown) {
		this.isBreakdown = isBreakdown;
	}

	public List<Field> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<Field> shortestPath) {
		this.shortestPath = shortestPath;
	}

	public Boolean getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public Boolean getIsDiscount() {
		return isDiscount;
	}

	public void setIsDiscount(Boolean isDiscount) {
		this.isDiscount = isDiscount;
	}

	public Field getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Field startLocation) {
		this.startLocation = startLocation;
	}

	public Field getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(Field endLocation) {
		this.endLocation = endLocation;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Long getDurationInSeconds() {
		return durationInSeconds;
	}

	public void setDurationInSeconds(Long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public static Integer getDiscount() {
		return discount;
	}

	public static void setDiscount(Integer discount) {
		Rental.discount = discount;
	}

	public static DateTimeFormatter getDateTimeFormatter() {
		return DATE_TIME_FORMATTER;
	}

	@Override
	public String toString() {
		return String.format(
				"Rental-> %s, vehicleId= %3s, startLocation= %7s, endLocation= %7s, startTime= %s, endTime= %s, durationInSeconds= %3d, isPromotion= %5s, isDiscount= %5s isBreakdown= %5s   ",
				user, vehicleId, startLocation != null ? startLocation : "N/A",
				endLocation != null ? endLocation : "N/A",
				startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "N/A",
				endTime != null ? endTime.format(DATE_TIME_FORMATTER) : "N/A", durationInSeconds, isPromotion,
				isDiscount, isBreakdown);
	}

}
