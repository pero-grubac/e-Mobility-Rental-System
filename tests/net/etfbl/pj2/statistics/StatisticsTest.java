package net.etfbl.pj2.statistics;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.support.Fixtures;

class StatisticsTest {

	private static BigDecimal money(String value) {
		return new BigDecimal(value);
	}

	private static Statistics stats(Invoice... invoices) {
		return new Statistics(List.of(invoices), Fixtures.CONF);
	}

	private static Invoice carRide(long seconds) {
		return Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 5), seconds, false, false);
	}

	@Test
	void totalIncomeIsTheSumOfInvoiceTotals() {
		assertEquals(money("200.00"), stats(carRide(4), carRide(6), carRide(10)).calculateTotalIncome());
	}

	@Test
	void noInvoicesMeansZeroEverywhere() {
		Statistics stats = stats();

		assertEquals(money("0.00"), stats.calculateTotalIncome());
		assertEquals(money("0.00"), stats.calculateTotalDiscount());
		assertEquals(money("0.00"), stats.calculateTotalPromotions());
		assertEquals(money("0.00"), stats.calculateTotalIncomeInNarrowArea());
		assertEquals(money("0.00"), stats.calculateTotalIncomeInWideArea());
		assertEquals(money("0.00"), stats.calculateTotalAmountForRepairs());
		assertEquals(money("0.00"), stats.calculateTotalTax());
	}

	@Test
	void discountsAndPromotionsAreSummedFromInvoices() {
		Invoice discounted = Fixtures.discountedInvoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 5), 10, true);
		Invoice promoted = Fixtures.invoice(Fixtures.car("A2", 50000), at(5, 5), at(5, 5), 10, true, false);

		Statistics stats = stats(discounted, promoted);

		assertEquals(money("5.00"), stats.calculateTotalDiscount(), "0.05 * 100");
		assertEquals(money("14.00"), stats.calculateTotalPromotions(), "2 * 0.07 * 100");
		assertEquals(money("181.00"), stats.calculateTotalIncome(), "88 + 93");
	}

	@Test
	void areaIncomeIsTheSumOfRideAmountsPerArea() {
		Invoice narrow = Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(14, 14), 4, false, false);
		Invoice wide = Fixtures.invoice(Fixtures.car("A2", 50000), at(0, 0), at(5, 5), 4, true, false);

		Statistics stats = stats(narrow, wide);

		assertEquals(money("40.00"), stats.calculateTotalIncomeInNarrowArea());
		assertEquals(money("60.00"), stats.calculateTotalIncomeInWideArea());
	}

	@Test
	void maintenanceAndCompanyCostsAreTwentyPercentOfIncome() {
		Statistics stats = stats(carRide(100));

		assertEquals(money("1000.00"), stats.calculateTotalIncome());
		assertEquals(money("200.00"), stats.calculateTotalAmountForMaintenance());
		assertEquals(money("200.00"), stats.calculateTotalCost());
	}

	@Test
	void taxIsTenPercentOfWhatIsLeftAfterCosts() {
		Invoice broken = Fixtures.invoice(Fixtures.scooter("T1", 1000), at(5, 5), at(5, 5), 10, false, true);

		Statistics stats = stats(carRide(100), broken);

		assertEquals(money("20.00"), stats.calculateTotalAmountForRepairs());
		assertEquals(money("58.00"), stats.calculateTotalTax(), "(1000 - 200 - 20 - 200) * 10%");
	}

	@Test
	void ridesWithoutBreakdownCostNothingToRepair() {
		assertEquals(money("0.00"), stats(carRide(10)).calculateTotalAmountForRepairs());
	}

	@Test
	void everyBreakdownIsRepairedAtTheCoefficientOfItsType() {
		Invoice car = Fixtures.invoice(Fixtures.car("A1", 50000), at(5, 5), at(5, 5), 10, false, true);
		Invoice bike = Fixtures.invoice(Fixtures.bike("B1", 3000), at(5, 5), at(5, 5), 10, false, true);
		Invoice scooter = Fixtures.invoice(Fixtures.scooter("T1", 1000), at(5, 5), at(5, 5), 10, false, true);

		assertEquals(money("3500.00"), stats(car).calculateTotalAmountForRepairs(), "0.07 * 50000");
		assertEquals(money("120.00"), stats(bike).calculateTotalAmountForRepairs(), "0.04 * 3000");
		assertEquals(money("20.00"), stats(scooter).calculateTotalAmountForRepairs(), "0.02 * 1000");
		assertEquals(money("3640.00"), stats(car, bike, scooter).calculateTotalAmountForRepairs());
	}

	@Test
	void twoBreakdownsOfTheSameVehicleAreRepairedTwice() {
		Invoice first = Fixtures.invoice(Fixtures.bike("B1", 3000), at(5, 5), at(5, 5), 10, false, true);
		Invoice second = Fixtures.invoice(Fixtures.bike("B1", 3000), at(5, 5), at(5, 5), 10, false, true);

		assertEquals(money("240.00"), stats(first, second).calculateTotalAmountForRepairs());
	}
}
