package net.etfbl.pj2.invoice;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.support.Fixtures;
import net.etfbl.pj2.support.TestConfig;

/**
 * Billing rules from the task (prices from app.properties):
 * amount = UNIT_PRICE * seconds * (DISTANCE_WIDE if any field is in the wide area, else DISTANCE_NARROW),
 * total = amount - DISCOUNT * amount - DISCOUNT_PROM * amount, and total = 0 when there was a breakdown.
 */
class InvoiceTest {

	@TempDir
	Path dir;

	private static BigDecimal money(String value) {
		return new BigDecimal(value);
	}

	@Test
	void taskExampleScooterInWideAreaWithPromotion() {
		Invoice invoice = Fixtures.invoice(Fixtures.scooter("T1", 1000), at(0, 0), at(7, 8), 5, true, false);

		assertEquals(money("10.0"), invoice.getBasePrice(), "2.0 * 5s");
		assertEquals(money("15.00"), invoice.getAmount(), "(2.0 * 5s) * 1.5");
		assertEquals(money("1.05"), invoice.getPromotion(), "0.07 * 15.00");
		assertEquals(money("13.95"), invoice.getTotalAmount());
	}

	@Test
	void unitPriceDependsOnVehicleType() {
		assertEquals(10.0, Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 5), 4, false, false).getUnitPrice());
		assertEquals(3.0, Fixtures.invoice(Fixtures.bike("B1", 3000), at(5, 5), at(5, 5), 4, false, false).getUnitPrice());
		assertEquals(2.0, Fixtures.invoice(Fixtures.scooter("T1", 1000), at(5, 5), at(5, 5), 4, false, false).getUnitPrice());
	}

	@Test
	void amountDoesNotDependOnRouteLength() {
		Invoice shortRide = Fixtures.invoice(Fixtures.scooter("T1", 1000), at(5, 5), at(5, 5), 10, false, false);
		Invoice longRide = Fixtures.invoice(Fixtures.scooter("T2", 1000), at(5, 5), at(14, 14), 10, false, false);

		assertEquals(money("20.00"), shortRide.getAmount(), "2.0 * 10s * 1.0");
		assertEquals(money("20.00"), longRide.getAmount());
	}

	@Test
	void oneWideFieldIsEnoughForTheWideTariff() {
		Invoice inner = Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(14, 14), 4, false, false);
		Invoice leaves = Fixtures.invoice(Fixtures.car("A2", 50000), at(5, 5), at(15, 5), 4, false, false);

		assertFalse(inner.isWideArea());
		assertEquals(money("40.00"), inner.getAmount());
		assertTrue(leaves.isWideArea());
		assertEquals(1, leaves.getWideFields());
		assertEquals(money("60.00"), leaves.getAmount(), "10.0 * 4s * 1.5");
	}

	@Test
	void discountIsAPercentageOfTheAmount() {
		Invoice invoice = Fixtures.discountedInvoice(Fixtures.scooter("T1", 1000), at(5, 5), at(5, 5), 10, false);

		assertEquals(money("1.00"), invoice.getDiscount(), "0.05 * 20.00");
		assertEquals(money("19.00"), invoice.getTotalAmount());
	}

	@Test
	void discountAndPromotionTogether() {
		Invoice invoice = Fixtures.discountedInvoice(Fixtures.scooter("T1", 1000), at(5, 5), at(5, 5), 10, true);

		assertEquals(money("1.00"), invoice.getDiscount());
		assertEquals(money("1.40"), invoice.getPromotion());
		assertEquals(money("17.60"), invoice.getTotalAmount(), "20.00 - 1.00 - 1.40");
	}

	@Test
	void breakdownMakesTheRideFree() {
		Rental rental = Fixtures.rental("A1", at(0, 0), at(7, 8), 60, true, true);
		rental.setDiscount(true);
		Invoice invoice = new Invoice(rental, Fixtures.car("A1", 50000), Fixtures.CONF);

		assertEquals(0, invoice.getBasePrice().signum());
		assertEquals(0, invoice.getAmount().signum());
		assertEquals(0, invoice.getDiscount().signum());
		assertEquals(0, invoice.getPromotion().signum());
		assertEquals(0, invoice.getTotalAmount().signum());
	}

	@Test
	void noDiscountOrPromotionUnlessTheRentalHasThem() {
		Invoice invoice = Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 5), 4, false, false);

		assertEquals(0, invoice.getDiscount().signum());
		assertEquals(0, invoice.getPromotion().signum());
		assertEquals(invoice.getAmount(), invoice.getTotalAmount());
	}

	@Test
	void routeFieldsAreCountedPerArea() {
		Invoice invoice = Fixtures.invoice(Fixtures.scooter("T1", 1000), at(0, 0), at(7, 8), 5, false, false);

		assertEquals(16, invoice.getNarrowFields() + invoice.getWideFields());
		assertTrue(invoice.getNarrowFields() > 0);
		assertTrue(invoice.getWideFields() > 0);
	}

	@Test
	void invoiceFileListsEveryItem() throws Exception {
		TestConfig conf = new TestConfig(dir);
		TransportVehicle scooter = Fixtures.scooter("T1", 1000);
		Invoice invoice = Fixtures.discountedInvoice(scooter, at(0, 0), at(7, 8), 5, true);
		invoice.setStartBatteryLevel(100.0);
		invoice.setEndBatteryLevel(68.0);

		Path file = invoice.writeToFile(conf);

		assertTrue(invoice.isGenerated());
		assertEquals("K1 - 1.6.2024 09.00.txt", file.getFileName().toString());
		String text = Files.readString(file, StandardCharsets.UTF_8);
		for (String expected : new String[] { "User:", "K1", "Vehicle:", "Scooter T1", "(0,0)", "(7,8)",
				"00:00:05", "16 fields", "100% -> 68%", "Base price:", "10.00", "x 1.50", "Amount:", "15.00",
				"Discount:", "-0.75", "Promotion:", "-1.05", "TOTAL:", "13.20", "Breakdown:", "No" }) {
			assertTrue(text.contains(expected), "missing '" + expected + "' in\n" + text);
		}
		assertFalse(text.contains("Driver's license"), "scooter users show no documents");
	}

	@Test
	void carInvoiceShowsTheDocumentsOfTheUser() throws Exception {
		TestConfig conf = new TestConfig(dir).withForeignUserShare(1.0);
		Invoice invoice = Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 6), 5, false, false);
		invoice.getRental().getUser().issueDocuments(conf);

		String text = invoice.toText(conf);

		assertTrue(text.contains("Passport:"), text);
		assertTrue(text.contains(invoice.getRental().getUser().getIdentificationDocument()), text);
		assertTrue(text.contains("Driver's license:"), text);
	}

	@Test
	void domesticUserShowsAnIdCard() {
		TestConfig conf = new TestConfig(dir).withForeignUserShare(0.0);
		Invoice invoice = Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 6), 5, false, false);
		invoice.getRental().getUser().issueDocuments(conf);

		assertTrue(invoice.toText(conf).contains("ID card:"));
	}

	@Test
	void breakdownIsWrittenOnTheInvoice() throws Exception {
		TestConfig conf = new TestConfig(dir);
		TransportVehicle car = Fixtures.car("A1", 50000);
		Invoice invoice = Fixtures.invoice(car, at(5, 5), at(5, 6), 5, false, true);
		invoice.setBreakdown(car.breakDown(Fixtures.START, "Flat tire"));

		String text = Files.readString(invoice.writeToFile(conf));

		assertTrue(text.contains("1.6.2024 09:00 - Flat tire"), text);
		assertTrue(text.contains("TOTAL:                0.00"), text);
	}
}
