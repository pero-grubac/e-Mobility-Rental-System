package net.etfbl.pj2.util;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.support.Fixtures;

class UtilTest {

	@ParameterizedTest(name = "({0},{1}) -> ({2},{3})")
	@CsvSource({ "0,0,7,8", "7,8,0,0", "10,1,12,18", "0,0,19,19", "19,0,0,19", "3,3,3,3", "5,5,5,6", "5,5,9,5" })
	void routeIsAShortestPathOfStraightLines(int x1, int y1, int x2, int y2) {
		List<Field> route = Util.calculateRoute(at(x1, y1), at(x2, y2));

		assertEquals(Math.abs(x2 - x1) + Math.abs(y2 - y1) + 1, route.size(), "route length");
		assertEquals(at(x1, y1), route.get(0));
		assertEquals(at(x2, y2), route.get(route.size() - 1));

		int turns = 0;
		for (int i = 1; i < route.size(); i++) {
			Field prev = route.get(i - 1);
			Field curr = route.get(i);
			assertEquals(1, Math.abs(curr.getX() - prev.getX()) + Math.abs(curr.getY() - prev.getY()),
					"one horizontal or vertical step: " + prev + " -> " + curr);
			if (i >= 2) {
				Field before = route.get(i - 2);
				boolean horizontalBefore = prev.getY() == before.getY();
				boolean horizontalNow = curr.getY() == prev.getY();
				if (horizontalBefore != horizontalNow)
					turns++;
			}
		}
		assertTrue(turns <= 1, "at most one turn, found " + turns);
	}

	@Test
	void generatedIdHasRequestedLengthAndOnlyLettersAndDigits() {
		String id = Util.generateId(25);
		assertEquals(25, id.length());
		assertTrue(id.matches("[A-Za-z0-9]+"));
	}

	@Test
	void generatedIdRejectsNonPositiveLength() {
		assertThrows(IllegalArgumentException.class, () -> Util.generateId(0));
	}

	@Test
	void everyNthRentalGetsTheDiscount() {
		List<Rental> rentals = new ArrayList<>();
		for (int i = 0; i < 25; i++) {
			rentals.add(Fixtures.rental("A1", at(5, 5), at(5, 5), 10, false, false));
		}

		Util.applyEveryNthDiscount(rentals, 10);

		for (int i = 0; i < rentals.size(); i++) {
			assertEquals((i + 1) % 10 == 0, rentals.get(i).isDiscount(), "rental #" + (i + 1));
		}
	}

	@Test
	void onlyCarUsersGetDocuments() {
		Car car = Fixtures.car("A1", 50000);
		ElectricScooter scooter = Fixtures.scooter("T1", 1000);
		Rental carRental = Fixtures.rental("A1", at(5, 5), at(6, 6), 10, false, false);
		Rental scooterRental = Fixtures.rental("T1", at(5, 5), at(6, 6), 10, false, false);

		Util.issueDocumentsToCarUsers(List.of(carRental, scooterRental), List.of(car, scooter), Fixtures.CONF);

		assertTrue(carRental.getUser().hasDocuments());
		assertEquals(10, carRental.getUser().getIdentificationDocument().length());
		assertEquals(10, carRental.getUser().getDriverLicenseNumber().length());
		assertFalse(scooterRental.getUser().hasDocuments());
	}

	@Test
	void invoicesAreMatchedToVehiclesByIdAndUnknownVehiclesAreSkipped() {
		Car car = Fixtures.car("A1", 50000);
		ElectricScooter scooter = Fixtures.scooter("T1", 1000);
		List<Rental> rentals = List.of(Fixtures.rental("T1", at(5, 5), at(5, 5), 10, false, false),
				Fixtures.rental("X9", at(5, 5), at(5, 5), 10, false, false),
				Fixtures.rental("A1", at(5, 5), at(5, 5), 10, false, false));

		List<Invoice> invoices = Util.createInvoices(rentals, List.<TransportVehicle>of(car, scooter), Fixtures.CONF);

		assertEquals(2, invoices.size());
		assertSame(scooter, invoices.get(0).getVehicle());
		assertSame(car, invoices.get(1).getVehicle());
	}

	@Test
	void invoicesAreGroupedByDayAndByStartTimeInChronologicalOrder() {
		Car car = Fixtures.car("A1", 50000);
		LocalDateTime day2 = LocalDateTime.of(2024, 6, 2, 9, 0);
		LocalDateTime day1Late = LocalDateTime.of(2024, 6, 1, 13, 0);
		LocalDateTime day1Early = LocalDateTime.of(2024, 6, 1, 9, 30);
		List<Invoice> invoices = new ArrayList<>();
		for (LocalDateTime start : List.of(day2, day1Late, day1Early, day1Early)) {
			invoices.add(new Invoice(Fixtures.rental("K1", "A1", start, at(5, 5), at(5, 5), 1, false, false), car,
					Fixtures.CONF));
		}

		Map<LocalDate, List<Invoice>> byDate = Util.groupByDate(invoices);
		Map<LocalDateTime, List<Invoice>> byTime = Util.groupByStartTime(invoices);

		assertEquals(List.of(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 2)), new ArrayList<>(byDate.keySet()));
		assertEquals(3, byDate.get(LocalDate.of(2024, 6, 1)).size());
		assertEquals(List.of(day1Early, day1Late, day2), new ArrayList<>(byTime.keySet()));
		assertEquals(2, byTime.get(day1Early).size(), "rentals at the same date and time run together");
	}
}
