package net.etfbl.pj2.model;

import java.time.LocalDateTime;
import java.util.List;

import net.etfbl.pj2.util.Util;

/**
 * One rental: who rented which vehicle, where it was taken and left, when and
 * for how long, and whether there was a promotion, a discount or a breakdown.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Rental {
	private final User user;
	private final String vehicleId;
	private final Field startLocation;
	private final Field endLocation;
	private final List<Field> route;
	private final LocalDateTime startTime;
	private final long durationInSeconds;
	private final boolean promotion;
	private final boolean breakdown;
	private boolean discount;

	/**
	 * Constructs a new rental and calculates its route.
	 *
	 * @param username          The name of the user.
	 * @param vehicleId         The ID of the rented vehicle.
	 * @param startLocation     Where the vehicle is taken.
	 * @param endLocation       Where the vehicle is left.
	 * @param startTime         When the rental starts.
	 * @param durationInSeconds How long the ride lasts.
	 * @param promotion         True if the rental has a promotion.
	 * @param breakdown         True if the vehicle breaks down.
	 */
	public Rental(String username, String vehicleId, Field startLocation, Field endLocation, LocalDateTime startTime,
			long durationInSeconds, boolean promotion, boolean breakdown) {
		this.user = new User(username);
		this.vehicleId = vehicleId;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.startTime = startTime;
		this.durationInSeconds = durationInSeconds;
		this.promotion = promotion;
		this.breakdown = breakdown;
		this.route = Util.calculateRoute(startLocation, endLocation);
	}

	public User getUser() {
		return user;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public Field getStartLocation() {
		return startLocation;
	}

	public Field getEndLocation() {
		return endLocation;
	}

	/**
	 * @return The fields the vehicle drives over, from start to end.
	 */
	public List<Field> getRoute() {
		return route;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * @return When the ride ends: the start time plus its duration.
	 */
	public LocalDateTime getEndTime() {
		return startTime.plusSeconds(durationInSeconds);
	}

	public long getDurationInSeconds() {
		return durationInSeconds;
	}

	public boolean isPromotion() {
		return promotion;
	}

	public boolean isBreakdown() {
		return breakdown;
	}

	/**
	 * @return True if this is a discounted (every N-th) rental.
	 */
	public boolean isDiscount() {
		return discount;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}

	@Override
	public String toString() {
		return user + " " + vehicleId + " " + startLocation + " -> " + endLocation + " at " + startTime;
	}
}
