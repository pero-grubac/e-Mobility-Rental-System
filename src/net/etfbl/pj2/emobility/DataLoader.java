package net.etfbl.pj2.emobility;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.RentalParser;
import net.etfbl.pj2.parser.VehicleParser;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

/**
 * Reads the input files and prepares everything the simulation needs.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public final class DataLoader {

	/**
	 * The vehicles of the company and the invoices of all valid rentals.
	 *
	 * @param vehicles The vehicles.
	 * @param invoices The invoices, sorted by start time.
	 */
	public record Data(List<TransportVehicle> vehicles, List<Invoice> invoices) {
	}

	private DataLoader() {
	}

	/**
	 * Parses the vehicles and the rentals, skips rentals of unknown vehicles,
	 * gives every N-th rental the discount, issues documents to car users and
	 * creates the invoices.
	 *
	 * @param conf The application configuration.
	 * @return The loaded data.
	 */
	public static Data load(AppConfig conf) {
		List<TransportVehicle> vehicles = new VehicleParser().parseVehicles(conf);
		Set<String> ids = vehicles.stream().map(TransportVehicle::getId).collect(Collectors.toSet());
		List<Rental> rentals = new RentalParser().parseRentals(conf, ids);
		Util.applyEveryNthDiscount(rentals, conf.getDiscountEveryNthRental());
		Util.issueDocumentsToCarUsers(rentals, vehicles, conf);
		return new Data(List.copyOf(vehicles), Util.createInvoices(rentals, vehicles, conf));
	}
}
