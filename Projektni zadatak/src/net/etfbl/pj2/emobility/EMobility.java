package net.etfbl.pj2.emobility;

import java.io.File;
import java.util.List;

import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.RentalParser;
import net.etfbl.pj2.parser.VehicleParser;
import net.etfbl.pj2.rental.Rental;

public class EMobility {
	public static void main(String[] args) {
		String vehiclesPath = "Test" + File.separator + "PJ2 - projektni zadatak 2024 - Prevozna sredstva.csv";
		VehicleParser vehicleParser = new VehicleParser();
		List<TransportVehicle> vehicles = vehicleParser.parseVehicles(vehiclesPath);
		for (TransportVehicle vehicle : vehicles) {
			System.out.println(vehicle);
		}
		System.out.println();
		String rentalPath = "Test" + File.separator + "PJ2 - projektni zadatak 2024 - Iznajmljivanja.csv";
		RentalParser rentalParser = new RentalParser();
		List<Rental> rentals = rentalParser.parseRentals(rentalPath);
		for (Rental rental : rentals) {
			System.out.println(rental);
		}
	}
}
