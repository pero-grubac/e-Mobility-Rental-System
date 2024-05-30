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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import net.etfbl.pj2.gui.MainFrame;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.RentalParser;
import net.etfbl.pj2.parser.VehicleParser;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.simulation.Simulation;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.statistics.SpecialReport;
import net.etfbl.pj2.statistics.SummaryReport;
import net.etfbl.pj2.util.Util;

public class EMobility {
	public static void main(String[] args) {
		AppConfig conf = new AppConfig();
		

		String vehiclesPath = conf.getTestFolder() + File.separator + conf.getTestVehicle();
		VehicleParser vehicleParser = new VehicleParser();
		List<TransportVehicle> vehicles = vehicleParser.parseVehicles(vehiclesPath);
		// vehicles.stream().forEach(v->System.out.println(v));

		String rentalPath = conf.getTestFolder() + File.separator + conf.getTestRental();
		RentalParser rentalParser = new RentalParser();
		List<Rental> rentals = rentalParser.parseRentals(rentalPath);
		// rentals.stream().forEach(v->System.out.println(v));
		Util.populateUser(rentals, vehicles);

		List<Invoice> invoice = Util.calculateInvoice(rentals, vehicles);
		// invoice.forEach(i->System.out.println(i.getRental().getUser().getName()));
		DailyReport dailyReport = new SummaryReport(invoice);
		LocalDate currentDate = LocalDate.now();
		// ReportFileManager.saveReportToTextFile(dailyReport, currentDate);
		List<String> summaryReports = ReportFileManager.loadReportFromTextFile(new SummaryReport());
		// summaryReports.forEach(report -> System.out.println(report));
		// Util.generateDailyReports(invoice);
		// invoice.forEach(i -> i.generateInvoice(conf));
		// List<String> dailyReports = ReportFileManager.loadDailyReportFromTextFile(new
		// DailyReport());
		// dailyReports.forEach(report -> System.out.println(report));
		// String newReport = ReportFileManager.loadDailyReportFromTextFile();
		// System.out.println(newReport);

		// SpecialReport sr = new SpecialReport(invoice);
		// ReportFileManager.saveReportToBinaryFile(sr);
		// System.out.println(ReportFileManager.loadReportFromBinaryFile());
		/*
		 * invoice.forEach(inv -> { System.out.println(inv.getRental().getVehicleId());
		 * System.out.println(inv.getRental().getDurationInSeconds());
		 * System.out.println(inv.getRental().getStartTime());
		 * inv.getRental().getShortestPath().forEach(f -> {
		 * 
		 * System.out.println(f);
		 * 
		 * }); System.out.println(); });
		 */

		/*
		 * Map<LocalDate, List<Invoice>> groupedInvoices =
		 * Util.groupeInvoicesByDate(invoice);
		 * 
		 * groupedInvoices.forEach((date, invoiceList) -> {
		 * System.out.println("Processing rentals for date: " + date);
		 * 
		 * Map<LocalDateTime, List<Invoice>> groupedInvoiceByTime =
		 * Util.groupeInvoicesByTime(invoiceList); groupedInvoiceByTime.forEach((time,
		 * list) -> { System.out.println("Processing rentals for time: " + time);
		 * 
		 * List<Invoice> sortedInvoices = list.stream() .sorted(Comparator.comparing(i
		 * -> i.getRental().getStartTime())).collect(Collectors.toList());
		 * List<CompletableFuture<Void>> futures = new ArrayList<>(); for (Invoice inv :
		 * list) { CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
		 * processInvoice(inv));
		 * 
		 * futures.add(future); } CompletableFuture<Void> allFutures =
		 * CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])); try {
		 * allFutures.get(); } catch (InterruptedException | ExecutionException e) {
		 * System.err.println("Error waiting for completion of invoices: " +
		 * e.getMessage()); } }); try { Thread.sleep(5000); } catch
		 * (InterruptedException e) { Thread.currentThread().interrupt();
		 * System.err.println("Thread interrupted: " + e.getMessage()); }
		 * 
		 * });
		 */

	/*	Simulation sim = new Simulation();
		sim.startSimulationWithCompletableFuture(invoice, conf);
		//invoice.forEach(i -> i.generateInvoice(conf));
		System.out.println("kraj");
*/
		MainFrame mainFrame = new MainFrame(conf);
		mainFrame.setVisible(true);
		Simulation sim = new Simulation();
		sim.startStimulationWithSemaphore(invoice, conf, mainFrame);
	}

	
}
