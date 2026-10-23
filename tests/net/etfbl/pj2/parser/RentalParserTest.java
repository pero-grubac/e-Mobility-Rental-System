package net.etfbl.pj2.parser;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.support.TestConfig;

class RentalParserTest {

	@TempDir
	Path dir;

	private final RentalParser parser = new RentalParser();

	@Test
	void parsesQuotedAndUnquotedLocations() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,T1,\"0,0\",\"7,8\",5,ne,da",
				"3.6.2024 09:00,K2,A1,5,5,\"11,5\",8,da,ne");

		List<Rental> rentals = parser.parseRentals(conf);

		assertEquals(2, rentals.size());
		Rental first = rentals.get(0);
		assertEquals(LocalDateTime.of(2024, 6, 1, 9, 0), first.getStartTime());
		assertEquals("K1", first.getUser().getName());
		assertEquals("T1", first.getVehicleId());
		assertEquals(at(0, 0), first.getStartLocation());
		assertEquals(at(7, 8), first.getEndLocation());
		assertEquals(5L, first.getDurationInSeconds());
		assertFalse(first.isBreakdown());
		assertTrue(first.isPromotion());

		Rental second = rentals.get(1);
		assertEquals(at(5, 5), second.getStartLocation());
		assertEquals(at(11, 5), second.getEndLocation());
		assertTrue(second.isBreakdown());
		assertFalse(second.isPromotion());
	}

	@Test
	void rentalsAreSortedByStartTime() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"3.6.2024 09:00,K1,A1,\"1,1\",\"2,2\",5,ne,ne",
				"1.6.2024 10:00,K2,B1,\"1,1\",\"2,2\",5,ne,ne",
				"1.6.2024 09:00,K3,T1,\"1,1\",\"2,2\",5,ne,ne");

		List<String> users = parser.parseRentals(conf).stream().map(r -> r.getUser().getName()).toList();

		assertEquals(List.of("K3", "K2", "K1"), users);
	}

	@Test
	void skipsInvalidLinesAndKeepsTheRest() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,T1,\"0,0\",\"20,8\",5,ne,ne",   // end outside the 20x20 map
				"1.6.2024 09:00,K2,T2,\"-1,0\",\"7,8\",5,ne,ne",   // start outside the map
				"1.6.2024 09:00,K3,T3,\"0,0\",\"7,8\",0,ne,ne",    // zero duration
				"1.6.2024 09:00,K4,T4,\"0,0\",\"7,8\",-4,ne,ne",   // negative duration
				"1.6.2024 09:00,K5,T5,\"0,0\",\"7,8\",5,mozda,ne", // not da/ne
				"32.6.2024 09:00,K6,T6,\"0,0\",\"7,8\",5,ne,ne",   // invalid date
				"1.6.2024 09:00,K7,T7,\"0,0\",7,5,ne,ne",          // missing column
				"1.6.2024 09:00,K8,T8,\"a,0\",\"7,8\",5,ne,ne",    // not a number
				"1.6.2024 09:00,,T10,\"0,0\",\"7,8\",5,ne,ne",     // no user
				"1.6.2024 09:00,K9,T9,\"0,0\",\"7,8\",5,ne,ne");

		List<Rental> rentals = parser.parseRentals(conf);

		assertEquals(1, rentals.size());
		assertEquals("K9", rentals.get(0).getUser().getName());
	}

	@Test
	void rentalsOfUnknownVehiclesAreSkippedWhenVehiclesAreGiven() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,T77,\"0,0\",\"1,1\",120,ne,ne",
				"1.6.2024 09:01,K1,T1,\"0,0\",\"1,1\",10,ne,ne");

		List<Rental> rentals = parser.parseRentals(conf, Set.of("T1"));

		assertEquals(1, rentals.size(), "the unknown vehicle must not block the user's real rental");
		assertEquals("T1", rentals.get(0).getVehicleId());
	}

	@Test
	void vehicleCannotBeRentedTwiceAtTheSameTime() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,A1,\"0,0\",\"1,1\",120,ne,ne",
				"1.6.2024 09:01,K2,A1,\"0,0\",\"1,1\",10,ne,ne");

		List<Rental> rentals = parser.parseRentals(conf);

		assertEquals(1, rentals.size());
		assertEquals("K1", rentals.get(0).getUser().getName());
	}

	@Test
	void userCannotRentTwoVehiclesAtTheSameTime() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,A1,\"0,0\",\"1,1\",120,ne,ne",
				"1.6.2024 09:01,K1,B1,\"0,0\",\"1,1\",10,ne,ne");

		assertEquals(1, parser.parseRentals(conf).size());
	}

	@Test
	void vehicleCanBeRentedAgainAfterThePreviousRentalEnds() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,A1,\"0,0\",\"1,1\",30,ne,ne",
				"1.6.2024 09:30,K2,A1,\"1,1\",\"2,2\",30,ne,ne");

		assertEquals(2, parser.parseRentals(conf).size());
	}

	@Test
	void sameUserCanRentTheSameVehicleOnAnotherDay() throws Exception {
		TestConfig conf = new TestConfig(dir).withRentals(
				"1.6.2024 09:00,K1,A1,\"0,0\",\"1,1\",30,ne,ne",
				"2.6.2024 09:00,K1,A1,\"1,1\",\"2,2\",30,ne,ne");

		assertEquals(2, parser.parseRentals(conf).size());
	}

	@Test
	void missingFileGivesEmptyList() {
		assertEquals(List.of(), parser.parseRentals(new TestConfig(dir)));
	}
}
