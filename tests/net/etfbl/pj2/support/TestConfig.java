package net.etfbl.pj2.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.etfbl.pj2.resources.AppConfig;

/**
 * {@link AppConfig} that keeps every price and rule from app.properties but
 * redirects input and output folders to a temporary directory, so tests never
 * touch the project's Test, Invoice or Report folders. The pause between time
 * slots is 0 so simulations finish quickly.
 */
public class TestConfig extends AppConfig {

	public static final String VEHICLE_HEADER = "ID,Proizvodjac,Model,Datum nabavke,Cijena,Domet,Max Brzina,Opis,Vrsta";
	public static final String RENTAL_HEADER = "Datum,Korisnik,ID prevoznog sredstva,Pocetna lokacija,Odrediste,Trajanje,Kvar,Promocija";

	private final Path dir;
	private double foreignUserShare = -1;

	/**
	 * @param dir The temporary directory used for test data and generated files.
	 */
	public TestConfig(Path dir) {
		this.dir = dir;
	}

	/**
	 * Writes a vehicle CSV (header included) that {@code VehicleParser} will read.
	 */
	public TestConfig withVehicles(String... lines) throws IOException {
		write(getVehiclesFile(), VEHICLE_HEADER, lines);
		return this;
	}

	/**
	 * Writes a rental CSV (header included) that {@code RentalParser} will read.
	 */
	public TestConfig withRentals(String... lines) throws IOException {
		write(getRentalsFile(), RENTAL_HEADER, lines);
		return this;
	}

	/**
	 * @param share The share of car users who show a passport (0 or 1 makes it
	 *              predictable).
	 */
	public TestConfig withForeignUserShare(double share) {
		this.foreignUserShare = share;
		return this;
	}

	private static void write(Path file, String header, String... lines) throws IOException {
		List<String> content = new ArrayList<>();
		content.add(header);
		content.addAll(Arrays.asList(lines));
		Files.write(file, content, StandardCharsets.UTF_8);
	}

	@Override
	public Path getVehiclesFile() {
		return dir.resolve("vehicles.csv");
	}

	@Override
	public Path getRentalsFile() {
		return dir.resolve("rentals.csv");
	}

	@Override
	public String getInvoiceFolder() {
		return dir.resolve("Invoice").toString();
	}

	@Override
	public String getReportFolder() {
		return dir.resolve("Report").toString();
	}

	@Override
	public int getPauseBetweenTimeSlots() {
		return 0;
	}

	@Override
	public double getForeignUserShare() {
		return foreignUserShare < 0 ? super.getForeignUserShare() : foreignUserShare;
	}
}
