package net.etfbl.pj2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.etfbl.pj2.emobility.DataLoader;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Loads the sample data in the Test folder the same way the application does.
 * Maven runs tests from the project folder, so the relative paths in
 * app.properties resolve as they do for the app.
 */
class SampleDataTest {

	private final AppConfig conf = new AppConfig();
	private final DataLoader.Data data = DataLoader.load(conf);

	@Test
	void sampleVehiclesContainAllThreeTypesWithUniqueIds() {
		List<TransportVehicle> vehicles = data.vehicles();

		assertEquals(18, vehicles.size(), "19 lines, one duplicate ID");
		for (VehicleType type : VehicleType.values()) {
			assertTrue(vehicles.stream().anyMatch(v -> v.getType() == type), type.toString());
		}
		assertEquals(vehicles.size(), vehicles.stream().map(TransportVehicle::getId).distinct().count());
	}

	@Test
	void sampleRentalsBecomeInvoicesInTimeOrder() {
		List<Invoice> invoices = data.invoices();

		assertFalse(invoices.isEmpty());
		assertEquals(invoices.stream().map(i -> i.getRental().getStartTime()).sorted(Comparator.naturalOrder()).toList(),
				invoices.stream().map(i -> i.getRental().getStartTime()).toList());
		assertTrue(invoices.stream().noneMatch(i -> i.getVehicle().getId().equals("T77")), "unknown vehicle skipped");
	}

	@Test
	void everyTenthRentalHasTheDiscount() {
		List<Invoice> invoices = data.invoices();

		for (int i = 0; i < invoices.size(); i++) {
			assertEquals((i + 1) % 10 == 0, invoices.get(i).getRental().isDiscount(), "rental #" + (i + 1));
		}
	}

	@Test
	void everyInvoiceFollowsTheBillingRules() {
		for (Invoice invoice : data.invoices()) {
			BigDecimal total = invoice.getAmount().subtract(invoice.getDiscount()).subtract(invoice.getPromotion());
			assertEquals(total, invoice.getTotalAmount());
			assertTrue(invoice.getTotalAmount().signum() >= 0);
			if (invoice.getRental().isBreakdown()) {
				assertEquals(0, invoice.getTotalAmount().signum(), "breakdown rides are free");
			}
			boolean car = invoice.getVehicle().getType() == VehicleType.CAR;
			assertEquals(car, invoice.getRental().getUser().hasDocuments(), "documents only for car rentals");
		}
	}
}
