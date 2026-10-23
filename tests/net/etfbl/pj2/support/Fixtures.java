package net.etfbl.pj2.support;

import java.time.LocalDate;
import java.time.LocalDateTime;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Small builders for vehicles, rentals and invoices used across tests.
 */
public final class Fixtures {

	public static final AppConfig CONF = new AppConfig();
	public static final LocalDateTime START = LocalDateTime.of(2024, 6, 1, 9, 0);

	private Fixtures() {
	}

	public static Car car(String id, double price) {
		return new Car(id, "A", "M1", price, LocalDate.of(2024, 5, 1), "test car");
	}

	public static ElectricBike bike(String id, double price) {
		return new ElectricBike(id, "B", "BM1", price, 100);
	}

	public static ElectricScooter scooter(String id, double price) {
		return new ElectricScooter(id, "T", "TM1", price, 50);
	}

	public static Field at(int x, int y) {
		return new Field(x, y);
	}

	public static Rental rental(String vehicleId, Field from, Field to, long seconds, boolean promotion,
			boolean breakdown) {
		return rental("K1", vehicleId, START, from, to, seconds, promotion, breakdown);
	}

	public static Rental rental(String user, String vehicleId, LocalDateTime start, Field from, Field to, long seconds,
			boolean promotion, boolean breakdown) {
		return new Rental(user, vehicleId, from, to, start, seconds, promotion, breakdown);
	}

	public static Invoice invoice(TransportVehicle vehicle, Field from, Field to, long seconds, boolean promotion,
			boolean breakdown) {
		return new Invoice(rental(vehicle.getId(), from, to, seconds, promotion, breakdown), vehicle, CONF);
	}

	/** An invoice of the N-th (discounted) rental. */
	public static Invoice discountedInvoice(TransportVehicle vehicle, Field from, Field to, long seconds,
			boolean promotion) {
		Rental rental = rental(vehicle.getId(), from, to, seconds, promotion, false);
		rental.setDiscount(true);
		return new Invoice(rental, vehicle, CONF);
	}
}
