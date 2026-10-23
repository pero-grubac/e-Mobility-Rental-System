package net.etfbl.pj2.simulation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.etfbl.pj2.breakdown.Breakdown;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.statistics.SpecialReport;
import net.etfbl.pj2.statistics.SummaryReport;
import net.etfbl.pj2.util.Util;

/**
 * Replays the rentals in chronological order. All rentals that start at the
 * same date and time run together, each in its own thread, on the shared map.
 * A ride lasts as many seconds as its duration: the duration is split evenly
 * over the fields of its route. When every rental of a time slot has ended,
 * the simulation pauses before the next time slot.
 * <p>
 * When a ride ends, its invoice is written. At the end of every day the daily
 * report is saved, and at the end the summary report and the serialized special
 * report.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Simulation implements Runnable {
	private final List<Invoice> invoices;
	private final AppConfig conf;
	private final SimulationListener listener;

	/**
	 * @param invoices The invoices of the rentals to simulate.
	 * @param conf     The application configuration.
	 * @param listener Receives the events of the simulation.
	 */
	public Simulation(List<Invoice> invoices, AppConfig conf, SimulationListener listener) {
		this.invoices = List.copyOf(invoices);
		this.conf = conf;
		this.listener = listener;
	}

	/**
	 * Runs the whole simulation and returns when it has finished.
	 */
	@Override
	public void run() {
		Map<LocalDate, List<Invoice>> byDate = Util.groupByDate(invoices);
		int slotsLeft = Util.groupByStartTime(invoices).size();

		try {
			for (Map.Entry<LocalDate, List<Invoice>> day : byDate.entrySet()) {
				for (Map.Entry<LocalDateTime, List<Invoice>> slot : Util.groupByStartTime(day.getValue()).entrySet()) {
					runTimeSlot(slot.getKey(), slot.getValue());
					if (--slotsLeft > 0) {
						pause();
					}
				}
				save(() -> ReportFileManager.saveDailyReport(new DailyReport(day.getKey(), day.getValue(), conf), conf));
			}
			save(() -> ReportFileManager.saveSummaryReport(new SummaryReport(invoices, conf), conf));
			save(() -> ReportFileManager.saveSpecialReport(new SpecialReport(invoices), conf));
			listener.finished();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Starts one thread per rental of the time slot and waits until all of them
	 * have ended.
	 *
	 * @param startTime The start date and time of the rentals.
	 * @param slot      The invoices of the rentals.
	 * @throws InterruptedException If the simulation is stopped.
	 */
	private void runTimeSlot(LocalDateTime startTime, List<Invoice> slot) throws InterruptedException {
		listener.timeSlotStarted(startTime, slot.size());
		List<Thread> threads = new ArrayList<>();
		for (Invoice invoice : slot) {
			Thread thread = new Thread(() -> ride(invoice),
					"rental-" + invoice.getRental().getUser() + "-" + invoice.getVehicle().getId());
			threads.add(thread);
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
	}

	/**
	 * Drives the vehicle of one rental along its route and writes the invoice.
	 *
	 * @param invoice The invoice of the rental.
	 */
	private void ride(Invoice invoice) {
		Rental rental = invoice.getRental();
		TransportVehicle vehicle = invoice.getVehicle();
		List<Field> route = rental.getRoute();
		long millisPerField = rental.getDurationInSeconds() * 1000 / route.size();

		invoice.setStartBatteryLevel(vehicle.getBatteryLevel());
		try {
			for (Field field : route) {
				vehicle.drainBattery();
				listener.vehicleMoved(invoice, field);
				Thread.sleep(millisPerField);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}
		invoice.setEndBatteryLevel(vehicle.getBatteryLevel());

		if (rental.isBreakdown()) {
			Breakdown breakdown = vehicle.breakDown(rental.getStartTime(), Util.randomElement(conf.getBreakdownReasons()));
			invoice.setBreakdown(breakdown);
		}
		save(() -> invoice.writeToFile(conf));
		listener.rentalFinished(invoice);
	}

	private void pause() throws InterruptedException {
		int seconds = conf.getPauseBetweenTimeSlots();
		listener.paused(seconds);
		Thread.sleep(seconds * 1000L);
	}

	/** A file operation that may fail. */
	@FunctionalInterface
	private interface FileTask {
		Object run() throws IOException;
	}

	private static void save(FileTask task) {
		try {
			task.run();
		} catch (IOException e) {
			System.err.println("File cannot be written: " + e.getMessage());
		}
	}
}
