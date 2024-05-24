package net.etfbl.pj2.emobility;

import java.io.File;
import java.util.List;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.RentalParser;
import net.etfbl.pj2.parser.VehicleParser;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.util.Util;

public class EMobility {
	public static void main(String[] args) {
		AppConfig conf = new AppConfig();
		
		String vehiclesPath = conf.getTestFolder() + File.separator + conf.getTestVehicle();
		VehicleParser vehicleParser = new VehicleParser();
		List<TransportVehicle> vehicles = vehicleParser.parseVehicles(vehiclesPath);
		
		String rentalPath = conf.getTestFolder() + File.separator + conf.getTestRental();
		RentalParser rentalParser = new RentalParser();
		List<Rental> rentals = rentalParser.parseRentals(rentalPath);
		Util.populateUser(rentals, vehicles);

		
		List<Invoice> invoice = Util.calculateInvoice(rentals, vehicles);

		DailyReport dailyReport = new DailyReport(invoice);
		ReportFileManager.saveReport(dailyReport);
		DailyReport newReport = ReportFileManager.readDailyReport();
		System.out.println(newReport);
	}
}
