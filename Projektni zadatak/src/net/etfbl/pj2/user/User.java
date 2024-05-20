package net.etfbl.pj2.user;

import java.util.List;

import net.etfbl.pj2.rental.Rental;

public class User {
	
	private String id;
	private String name;
	private String identificationDocument;
	private String driverLicenseNumber;
	private List<Rental> rentalHistory;
}
