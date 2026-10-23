package net.etfbl.pj2.simulation;

import java.time.LocalDateTime;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;

/**
 * Receives the events of a running {@link Simulation}. The methods are called
 * from simulation threads, so an implementation that updates a GUI must hand
 * the work over to its own thread.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public interface SimulationListener {

	/**
	 * All rentals that start at this date and time are about to start.
	 *
	 * @param startTime The date and time.
	 * @param rentals   The number of rentals that start.
	 */
	default void timeSlotStarted(LocalDateTime startTime, int rentals) {
	}

	/**
	 * A vehicle moved to the next field of its route.
	 *
	 * @param invoice The invoice of the rental.
	 * @param field   The field the vehicle is on now.
	 */
	default void vehicleMoved(Invoice invoice, Field field) {
	}

	/**
	 * A rental has ended and its invoice has been written.
	 *
	 * @param invoice The invoice.
	 */
	default void rentalFinished(Invoice invoice) {
	}

	/**
	 * The simulation waits before the next time slot.
	 *
	 * @param seconds The length of the pause.
	 */
	default void paused(int seconds) {
	}

	/**
	 * All rentals have been simulated and the reports saved.
	 */
	default void finished() {
	}
}
