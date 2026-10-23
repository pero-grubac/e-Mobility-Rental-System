package net.etfbl.pj2.resources;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.VehicleType;

class AppConfigTest {

	private final AppConfig conf = new AppConfig();

	@Test
	void loadsPricingParametersFromThePropertiesFile() {
		assertAll(() -> assertEquals(10.0, conf.getUnitPrice(VehicleType.CAR)),
				() -> assertEquals(3.0, conf.getUnitPrice(VehicleType.BIKE)),
				() -> assertEquals(2.0, conf.getUnitPrice(VehicleType.SCOOTER)),
				() -> assertEquals(0.0, conf.getBreakdownUnitPrice()),
				() -> assertEquals(1.0, conf.getDistanceNarrow()),
				() -> assertEquals(1.5, conf.getDistanceWide()),
				() -> assertEquals(0.05, conf.getDiscount()),
				() -> assertEquals(10, conf.getDiscountEveryNthRental()),
				() -> assertEquals(0.07, conf.getDiscountProm()));
	}

	@Test
	void repairCoefficientsMatchTheTask() {
		assertAll(() -> assertEquals(0.07, conf.getRepairCoefficient(VehicleType.CAR)),
				() -> assertEquals(0.04, conf.getRepairCoefficient(VehicleType.BIKE)),
				() -> assertEquals(0.02, conf.getRepairCoefficient(VehicleType.SCOOTER)));
	}

	@Test
	void businessResultRatesMatchTheTask() {
		assertAll(() -> assertEquals(0.2, conf.getMaintenanceRate()),
				() -> assertEquals(0.2, conf.getCompanyCostRate()),
				() -> assertEquals(0.1, conf.getTaxRate()),
				() -> assertEquals(5, conf.getPauseBetweenTimeSlots()));
	}

	@ParameterizedTest(name = "({0},{1}) narrow={2}")
	@CsvSource({ "5,5,true", "14,14,true", "5,14,true", "10,10,true", "4,5,false", "5,4,false", "15,14,false",
			"14,15,false", "0,0,false", "19,19,false" })
	void narrowAreaIsTheInnerBlockFrom5To14(int x, int y, boolean expected) {
		assertEquals(expected, conf.isNarrowArea(new Field(x, y)));
	}

	@ParameterizedTest(name = "({0},{1}) on map={2}")
	@CsvSource({ "0,0,true", "19,19,true", "0,19,true", "-1,0,false", "0,-1,false", "20,0,false", "0,20,false" })
	void mapIs20x20(int x, int y, boolean expected) {
		assertEquals(expected, conf.isOnMap(new Field(x, y)));
	}

	@ParameterizedTest
	@EnumSource(VehicleType.class)
	void everyVehicleTypeHasAllItsKeys(VehicleType type) {
		assertAll(() -> assertDoesNotThrow(() -> conf.getUnitPrice(type)),
				() -> assertDoesNotThrow(() -> conf.getRepairCoefficient(type)),
				() -> assertDoesNotThrow(() -> conf.getBatteryDrain(type)),
				() -> assertDoesNotThrow(() -> conf.getCsvVehicleType(type)),
				() -> assertDoesNotThrow(() -> java.awt.Color.decode(conf.getVehicleColor(type))),
				() -> assertFalse(conf.getVehicleTypeName(type).isBlank()));
	}

	@Test
	void vehicleTypeNamesAreNotSwapped() {
		assertEquals("Car", conf.getVehicleTypeName(VehicleType.CAR));
		assertEquals("Bike", conf.getVehicleTypeName(VehicleType.BIKE));
		assertEquals("Scooter", conf.getVehicleTypeName(VehicleType.SCOOTER));
	}

	@ParameterizedTest
	@ValueSource(strings = { "WIDE_AREA_COLOR", "NARROW_AREA_COLOR", "MAP_GRID_COLOR", "BACKGROUND_COLOR",
			"ACCENT_COLOR", "TEXT_COLOR", "MUTED_TEXT_COLOR", "POSITIVE_COLOR", "NEGATIVE_COLOR" })
	void everyColorIsAValidHexCode(String key) {
		assertDoesNotThrow(() -> conf.getColor(key));
	}

	@Test
	void datesOfTheInputFilesParse() {
		assertEquals(LocalDateTime.of(2024, 6, 1, 9, 0),
				LocalDateTime.parse("1.6.2024 09:00", conf.getRentalDateTimeFormatter()));
		assertEquals(LocalDate.of(2024, 5, 16), LocalDate.parse("16.5.2024.", conf.getPurchaseDateFormatter()));
	}

	@Test
	void breakdownReasonsAreAvailable() {
		assertFalse(conf.getBreakdownReasons().isEmpty());
		assertTrue(conf.getBreakdownReasons().stream().noneMatch(String::isBlank));
	}

	@Test
	void missingKeyIsReportedByName() {
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> conf.getProperty("NO_SUCH_KEY"));
		assertTrue(e.getMessage().contains("NO_SUCH_KEY"));
	}
}
