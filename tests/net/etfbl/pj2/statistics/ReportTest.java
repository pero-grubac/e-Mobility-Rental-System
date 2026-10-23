package net.etfbl.pj2.statistics;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.support.Fixtures;
import net.etfbl.pj2.support.TestConfig;

class ReportTest {

	@TempDir
	Path dir;

	private static Invoice ride(TransportVehicle vehicle, long seconds) {
		return Fixtures.invoice(vehicle, at(5, 5), at(5, 5), seconds, false, false);
	}

	@Test
	void dailyReportHasItemsOneToSix() {
		List<Invoice> invoices = List.of(ride(Fixtures.car("A1", 50000), 4), ride(Fixtures.bike("B1", 3000), 4),
				Fixtures.invoice(Fixtures.scooter("T1", 1000), at(0, 0), at(1, 1), 10, false, true));

		DailyReport report = new DailyReport(LocalDate.of(2024, 6, 1), invoices, Fixtures.CONF);

		assertEquals(3, report.getRentals());
		assertEquals(new BigDecimal("52.00"), report.getTotalIncome());
		assertEquals(new BigDecimal("0.00"), report.getTotalDiscount());
		assertEquals(new BigDecimal("0.00"), report.getTotalPromotions());
		assertEquals(new BigDecimal("52.00"), report.getTotalIncomeInNarrowArea());
		assertEquals(new BigDecimal("0.00"), report.getTotalIncomeInWideArea(), "the broken ride is free");
		assertEquals(new BigDecimal("10.40"), report.getTotalAmountForMaintenance());
		assertEquals(new BigDecimal("20.00"), report.getTotalAmountForRepairs());
	}

	@Test
	void summaryReportAddsCompanyCostsAndTax() {
		SummaryReport report = new SummaryReport(List.of(ride(Fixtures.car("A1", 50000), 100)), Fixtures.CONF);

		assertEquals(new BigDecimal("1000.00"), report.getTotalIncome());
		assertEquals(new BigDecimal("200.00"), report.getTotalCost());
		BigDecimal expectedTax = report.getTotalIncome().subtract(report.getTotalAmountForMaintenance())
				.subtract(report.getTotalAmountForRepairs()).subtract(report.getTotalCost())
				.multiply(new BigDecimal("0.1")).setScale(2, RoundingMode.HALF_UP);
		assertEquals(expectedTax, report.getTotalTax());
	}

	@Test
	void reportFilesListEveryItem() throws Exception {
		TestConfig conf = new TestConfig(dir);
		List<Invoice> invoices = List.of(ride(Fixtures.car("A1", 50000), 4));

		Path daily = ReportFileManager.saveDailyReport(new DailyReport(LocalDate.of(2024, 6, 1), invoices, conf),
				conf);
		Path summary = ReportFileManager.saveSummaryReport(new SummaryReport(invoices, conf), conf);

		assertEquals("DailyReport - 01-06-2024.txt", daily.getFileName().toString());
		String dailyText = Files.readString(daily);
		for (String label : List.of("Total income:", "Total discount:", "Total promotions:",
				"Income in narrow area:", "Income in wide area:", "Maintenance:", "Repairs:")) {
			assertTrue(dailyText.contains(label), "missing " + label + " in\n" + dailyText);
		}
		String summaryText = Files.readString(summary);
		assertTrue(summaryText.contains("Company costs:"), summaryText);
		assertTrue(summaryText.contains("Tax:"), summaryText);
	}

	@Test
	void specialReportAddsUpAllRentalsOfAVehicle() {
		Car a1 = Fixtures.car("A1", 50000);
		List<Invoice> invoices = List.of(ride(a1, 6), ride(a1, 6), ride(Fixtures.car("A2", 35000), 10),
				ride(Fixtures.bike("B1", 3000), 4), ride(Fixtures.bike("B2", 3000), 2),
				ride(Fixtures.scooter("T1", 1000), 4));

		List<VehicleRevenue> top = new SpecialReport(invoices).getTopVehicles();

		assertEquals(3, top.size());
		assertEquals("A1", top.get(0).getVehicle().getId());
		assertEquals(new BigDecimal("120.00"), top.get(0).getRevenue());
		assertEquals(2, top.get(0).getRentals());
		assertEquals("B1", top.get(1).getVehicle().getId());
		assertEquals("T1", top.get(2).getVehicle().getId());
	}

	@Test
	void specialReportSkipsTypesWithoutRentals() {
		List<VehicleRevenue> top = new SpecialReport(List.of(ride(Fixtures.bike("B1", 3000), 4))).getTopVehicles();

		assertEquals(1, top.size());
		assertEquals(VehicleType.BIKE, top.get(0).getVehicle().getType());
	}

	@Test
	void everyTopVehicleIsSerializedToItsOwnFileAndReadBack() throws Exception {
		TestConfig conf = new TestConfig(dir);
		ElectricBike bike = Fixtures.bike("B1", 3000);
		bike.breakDown(LocalDateTime.of(2024, 6, 1, 10, 0), "Flat tire");
		SpecialReport report = new SpecialReport(List.of(ride(Fixtures.car("A1", 50000), 4), ride(bike, 4),
				ride(Fixtures.scooter("T1", 1000), 4)));

		List<Path> files = ReportFileManager.saveSpecialReport(report, conf);
		List<VehicleRevenue> loaded = ReportFileManager.loadSpecialReport(conf);

		assertEquals(3, files.size());
		files.forEach(f -> assertTrue(Files.isRegularFile(f) && f.toString().endsWith(".ser"), f.toString()));
		assertEquals(3, loaded.size());
		assertEquals("A1", assertInstanceOf(Car.class, loaded.get(0).getVehicle()).getId());
		assertEquals(new BigDecimal("40.00"), loaded.get(0).getRevenue());
		ElectricBike loadedBike = assertInstanceOf(ElectricBike.class, loaded.get(1).getVehicle());
		assertEquals(100, loadedBike.getRangePerCharge());
		assertEquals("Flat tire", loadedBike.getBreakdowns().get(0).getDescription());
	}

	@Test
	void savingAgainReplacesTheOldFiles() throws Exception {
		TestConfig conf = new TestConfig(dir);
		ReportFileManager.saveSpecialReport(new SpecialReport(List.of(ride(Fixtures.car("A1", 50000), 4),
				ride(Fixtures.scooter("T1", 1000), 4))), conf);

		ReportFileManager.saveSpecialReport(new SpecialReport(List.of(ride(Fixtures.car("A2", 50000), 4))), conf);

		List<VehicleRevenue> loaded = ReportFileManager.loadSpecialReport(conf);
		assertEquals(1, loaded.size());
		assertEquals("A2", loaded.get(0).getVehicle().getId());
	}

	@Test
	void nothingSavedYetGivesAnEmptyReport() throws Exception {
		assertEquals(List.of(), ReportFileManager.loadSpecialReport(new TestConfig(dir)));
	}
}
