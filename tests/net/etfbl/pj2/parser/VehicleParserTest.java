package net.etfbl.pj2.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.support.TestConfig;

class VehicleParserTest {

	@TempDir
	Path dir;

	private final VehicleParser parser = new VehicleParser();

	@Test
	void parsesAllThreeVehicleTypesWithTheirFields() throws Exception {
		TestConfig conf = new TestConfig(dir).withVehicles(
				"A1,A,M1,1.5.2024.,50000,,,primjer 1,automobil",
				"B1,B,BM1,,3000,100,,,bicikl",
				"T1,T,TM1,,1000,,50,,trotinet");

		List<TransportVehicle> vehicles = parser.parseVehicles(conf);

		assertEquals(3, vehicles.size());
		Car car = assertInstanceOf(Car.class, vehicles.get(0));
		assertEquals("A1", car.getId());
		assertEquals("A", car.getManufacturer());
		assertEquals("M1", car.getModel());
		assertEquals(LocalDate.of(2024, 5, 1), car.getPurchaseDate());
		assertEquals(50000.0, car.getPurchasePrice());
		assertEquals("primjer 1", car.getDescription());
		assertEquals(100.0, car.getBatteryLevel());

		ElectricBike bike = assertInstanceOf(ElectricBike.class, vehicles.get(1));
		assertEquals(100, bike.getRangePerCharge());
		assertEquals(3000.0, bike.getPurchasePrice());

		ElectricScooter scooter = assertInstanceOf(ElectricScooter.class, vehicles.get(2));
		assertEquals(50, scooter.getMaxSpeed());
		assertEquals(1000.0, scooter.getPurchasePrice());
	}

	@Test
	void vehicleTypeIsCaseInsensitive() throws Exception {
		TestConfig conf = new TestConfig(dir).withVehicles("T1,T,TM1,,1000,,50,,TROTINET");

		assertInstanceOf(ElectricScooter.class, parser.parseVehicles(conf).get(0));
	}

	@Test
	void skipsDuplicateIdsAndKeepsTheFirst() throws Exception {
		TestConfig conf = new TestConfig(dir).withVehicles(
				"A1,A,M1,1.5.2024.,50000,,,first,automobil",
				"A1,A,M2,1.5.2024.,35000,,,second,automobil");

		List<TransportVehicle> vehicles = parser.parseVehicles(conf);

		assertEquals(1, vehicles.size());
		assertEquals("first", ((Car) vehicles.get(0)).getDescription());
	}

	@Test
	void skipsInvalidLinesAndKeepsTheRest() throws Exception {
		TestConfig conf = new TestConfig(dir).withVehicles(
				"X1,X,XM1,,1000,,,,brod",                  // unknown type
				"B2,B,BM2,,-3000,100,,,bicikl",            // negative price
				"B3,B,BM3,,abc,100,,,bicikl",              // price is not a number
				"B4,,BM4,,3000,100,,,bicikl",              // missing manufacturer
				"B5,B,BM5,,3000,100,bicikl",               // too few columns
				"B6,B,BM6,,3000,-5,,,bicikl",              // negative range
				"A9,A,M9,31.13.2024.,50000,,,x,automobil", // invalid date
				"",                                        // empty line
				"T1,T,TM1,,1000,,50,,trotinet");

		List<TransportVehicle> vehicles = parser.parseVehicles(conf);

		assertEquals(1, vehicles.size());
		assertEquals("T1", vehicles.get(0).getId());
	}

	@Test
	void missingFileGivesEmptyList() {
		assertEquals(List.of(), parser.parseVehicles(new TestConfig(dir)));
	}

	@Test
	void carWithoutPurchaseDateIsAccepted() throws Exception {
		TestConfig conf = new TestConfig(dir).withVehicles("A3,A,M3,,40000,,,bez datuma,automobil");

		Car car = (Car) parser.parseVehicles(conf).get(0);

		assertNull(car.getPurchaseDate());
	}
}
