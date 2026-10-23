package net.etfbl.pj2.simulation;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.support.Fixtures;
import net.etfbl.pj2.support.TestConfig;

/**
 * Runs the real simulation without a GUI. Rides last one or two seconds and
 * the pause between time slots is 0.
 */
class SimulationTest {

	@TempDir
	Path dir;

	/** Records every event in the order it arrives. */
	private static final class Recorder implements SimulationListener {
		final List<String> events = new CopyOnWriteArrayList<>();
		final Map<Invoice, List<Field>> moves = new ConcurrentHashMap<>();
		final AtomicInteger running = new AtomicInteger();
		final AtomicInteger maxRunning = new AtomicInteger();

		@Override
		public void timeSlotStarted(LocalDateTime startTime, int rentals) {
			events.add("slot " + startTime + " x" + rentals);
		}

		@Override
		public void vehicleMoved(Invoice invoice, Field field) {
			List<Field> fields = moves.computeIfAbsent(invoice, i -> {
				maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
				return Collections.synchronizedList(new ArrayList<>());
			});
			fields.add(field);
		}

		@Override
		public void rentalFinished(Invoice invoice) {
			running.decrementAndGet();
			events.add("finished " + invoice.getVehicle().getId());
		}

		@Override
		public void finished() {
			events.add("done");
		}
	}

	private static Invoice invoice(TransportVehicle vehicle, String user, LocalDateTime start, Field from, Field to,
			long seconds, boolean breakdown) {
		return new Invoice(Fixtures.rental(user, vehicle.getId(), start, from, to, seconds, false, breakdown), vehicle,
				Fixtures.CONF);
	}

	@Test
	void simulatesTimeSlotsInOrderWithParallelRidesAndWritesAllFiles() throws Exception {
		TestConfig conf = new TestConfig(dir);
		LocalDateTime day1 = LocalDateTime.of(2024, 6, 1, 9, 0);
		LocalDateTime day1Later = day1.plusMinutes(30);
		LocalDateTime day2 = day1.plusDays(1);
		TransportVehicle car = Fixtures.car("A1", 50000);
		TransportVehicle scooter = Fixtures.scooter("T1", 1000);
		TransportVehicle bike = Fixtures.bike("B1", 3000);
		List<Invoice> invoices = List.of(
				invoice(bike, "K3", day2, at(1, 1), at(1, 2), 1, false),
				invoice(car, "K1", day1, at(0, 0), at(3, 0), 2, false),
				invoice(scooter, "K2", day1, at(5, 5), at(5, 7), 2, true),
				invoice(car, "K1", day1Later, at(3, 0), at(3, 1), 1, false));
		Recorder recorder = new Recorder();

		assertTimeoutPreemptively(Duration.ofSeconds(20), () -> new Simulation(invoices, conf, recorder).run());

		assertEquals(List.of("slot " + day1 + " x2", "slot " + day1Later + " x1", "slot " + day2 + " x1"),
				recorder.events.stream().filter(e -> e.startsWith("slot")).toList(), "time slots in order");
		assertEquals("done", recorder.events.get(recorder.events.size() - 1));
		assertEquals(2, recorder.maxRunning.get(), "the two rentals of 09:00 ran at the same time");
		assertTrue(recorder.events.indexOf("slot " + day1Later + " x1") > recorder.events.indexOf("finished T1"),
				"the next time slot waits for every rental of the previous one");

		for (Invoice invoice : invoices) {
			assertEquals(invoice.getRental().getRoute(), recorder.moves.get(invoice), "drove the whole route");
			assertTrue(invoice.isGenerated());
		}
		assertTrue(invoices.get(1).getEndBatteryLevel() < invoices.get(1).getStartBatteryLevel(), "battery drained");

		assertNotNull(invoices.get(2).getBreakdown(), "the scooter broke down");
		assertEquals(1, scooter.getBreakdowns().size());
		assertTrue(conf.getBreakdownReasons().contains(scooter.getBreakdowns().get(0).getDescription()));
		assertNull(invoices.get(1).getBreakdown());

		try (var files = Files.list(Paths.get(conf.getInvoiceFolder()))) {
			assertEquals(4, files.count(), "one invoice per rental");
		}
		Path reports = Paths.get(conf.getReportFolder());
		assertTrue(Files.exists(reports.resolve("DailyReport - 01-06-2024.txt")));
		assertTrue(Files.exists(reports.resolve("DailyReport - 02-06-2024.txt")));
		assertTrue(Files.exists(reports.resolve("SummaryReport.txt")));
		assertEquals(3, ReportFileManager.loadSpecialReport(conf).size(), "top car, bike and scooter serialized");
	}

	@Test
	void rideLastsAsLongAsItsDuration() {
		TestConfig conf = new TestConfig(dir);
		Invoice invoice = invoice(Fixtures.bike("B4", 2700), "K5", Fixtures.START, at(7, 9), at(7, 11), 2, false);

		long start = System.nanoTime();
		new Simulation(List.of(invoice), conf, new SimulationListener() {
		}).run();
		long millis = (System.nanoTime() - start) / 1_000_000;

		assertTrue(millis >= 1900 && millis < 4000, "2 s ride over 3 fields took " + millis + " ms");
	}
}
