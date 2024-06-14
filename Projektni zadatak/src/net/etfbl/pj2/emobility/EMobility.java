package net.etfbl.pj2.emobility;


import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import net.etfbl.pj2.gui.MainFrame;
import net.etfbl.pj2.gui.SpecialReportFrame;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.DailyReportParser;
import net.etfbl.pj2.parser.RentalParser;
import net.etfbl.pj2.parser.SummaryReportParser;
import net.etfbl.pj2.parser.VehicleParser;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.simulation.Simulation;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.statistics.SpecialReport;
import net.etfbl.pj2.statistics.SummaryReport;
import net.etfbl.pj2.util.Util;
/**
 * The main class for the EMobility application.
 * This class initializes configurations, parses vehicle and rental data,
 * calculates invoices, and starts the simulation.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class EMobility {
	/**
     * The main method that starts the EMobility application.
     * It sets up the application configuration, parses vehicle and rental data,
     * populates users, calculates invoices, and starts the simulation.
     * 
     * @param args Command line arguments (not used).
     */
	public static void main(String[] args) {
		AppConfig conf = new AppConfig();
		LocalDate currentDate = LocalDate.now();

		VehicleParser vehicleParser = new VehicleParser();
		List<TransportVehicle> vehicles = vehicleParser.parseVehicles(conf);
		
		RentalParser rentalParser = new RentalParser();
		List<Rental> rentals = rentalParser.parseRentals(conf);
		 Set<String> validVehicleIds = vehicles.stream()
                 .map(TransportVehicle::getId)
                 .collect(Collectors.toSet());
		 rentals = rentals.stream()
                 .filter(rental -> validVehicleIds.contains(rental.getVehicleId()))
                 .collect(Collectors.toList());
		Util.populateUser(rentals, vehicles);

		List<Invoice> invoices = Util.calculateInvoice(rentals, vehicles);
		
		MainFrame mainFrame = new MainFrame(conf, invoices);
		mainFrame.setVisible(true);
		Simulation sim = new Simulation();
		sim.startStimulationWithSemaphore(invoices, conf, mainFrame);

	}

}
