package net.etfbl.pj2.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.etfbl.pj2.breakdown.Breakdown;
import net.etfbl.pj2.support.Fixtures;

class VehicleTest {

	static Stream<Arguments> vehiclesWithDrain() {
		return Stream.of(Arguments.of(Fixtures.car("A1", 50000), 5.0),
				Arguments.of(Fixtures.bike("B1", 3000), 3.0),
				Arguments.of(Fixtures.scooter("T1", 1000), 2.0));
	}

	@ParameterizedTest
	@MethodSource("vehiclesWithDrain")
	void newVehicleStartsWithFullBattery(TransportVehicle vehicle, double drain) {
		assertEquals(100.0, vehicle.getBatteryLevel());
		assertEquals("100%", vehicle.getBatteryText());
	}

	@ParameterizedTest
	@MethodSource("vehiclesWithDrain")
	void drivingOneFieldDrainsBatteryByTypeSpecificAmount(TransportVehicle vehicle, double drain) {
		vehicle.drainBattery();
		assertEquals(100.0 - drain, vehicle.getBatteryLevel());
	}

	@ParameterizedTest
	@MethodSource("vehiclesWithDrain")
	void emptyBatteryIsRechargedBeforeDraining(TransportVehicle vehicle, double drain) {
		vehicle.setBatteryLevel(drain);
		vehicle.drainBattery();
		assertEquals(100.0 - drain, vehicle.getBatteryLevel());
	}

	@ParameterizedTest
	@MethodSource("vehiclesWithDrain")
	void chargingIncreasesBatteryToMaximum(TransportVehicle vehicle, double drain) {
		vehicle.setBatteryLevel(12.0);
		vehicle.chargeBattery();
		assertEquals(100.0, vehicle.getBatteryLevel());
	}

	@ParameterizedTest
	@MethodSource("vehiclesWithDrain")
	void batteryStaysInRangeDuringALongRide(TransportVehicle vehicle, double drain) {
		for (int i = 0; i < 200; i++) {
			vehicle.drainBattery();
			assertTrue(vehicle.getBatteryLevel() > 0 && vehicle.getBatteryLevel() <= 100,
					"battery " + vehicle.getBatteryLevel());
		}
	}

	@Test
	void onlyCarsCarryMorePeople() {
		assertTrue(Fixtures.car("A1", 1).canCarryMultiplePeople());
		assertFalse(Fixtures.bike("B1", 1).canCarryMultiplePeople());
		assertFalse(Fixtures.scooter("T1", 1).canCarryMultiplePeople());
	}

	@Test
	void breakdownsAreRecordedWithTimeAndReason() {
		TransportVehicle scooter = Fixtures.scooter("T1", 1000);
		LocalDateTime time = LocalDateTime.of(2024, 6, 1, 9, 30);

		Breakdown first = scooter.breakDown(time, "Flat tire");
		scooter.breakDown(time.plusDays(1), "Brake malfunction");

		assertEquals(2, scooter.getBreakdowns().size());
		assertEquals(first, scooter.getBreakdowns().get(0));
		assertEquals(time, first.getDateTime());
		assertEquals("Flat tire", first.getDescription());
	}

	@Test
	void vehiclesAreEqualByIdOnly() {
		Car a = Fixtures.car("A1", 50000);
		Car sameId = Fixtures.car("A1", 1);
		Car other = Fixtures.car("A2", 50000);

		assertEquals(a, sameId);
		assertEquals(a.hashCode(), sameId.hashCode());
		assertNotEquals(a, other);
		assertNotEquals(a, Fixtures.bike("A1", 50000), "different vehicle types are never equal");
	}

	@Test
	void vehicleSurvivesSerializationWithAllFieldsAndBreakdowns() throws Exception {
		Car car = Fixtures.car("A1", 50000);
		car.setBatteryLevel(42.0);
		car.breakDown(LocalDateTime.of(2024, 6, 1, 10, 0), "Battery failure");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
			out.writeObject(car);
		}
		Car copy;
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
			copy = (Car) in.readObject();
		}

		assertEquals("A1", copy.getId());
		assertEquals("A", copy.getManufacturer());
		assertEquals("M1", copy.getModel());
		assertEquals(50000.0, copy.getPurchasePrice());
		assertEquals(42.0, copy.getBatteryLevel());
		assertEquals(car.getPurchaseDate(), copy.getPurchaseDate());
		assertEquals("test car", copy.getDescription());
		assertEquals("Battery failure", copy.getBreakdowns().get(0).getDescription());
	}
}
