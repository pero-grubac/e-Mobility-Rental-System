package net.etfbl.pj2.model;

import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

public class User {

	private String name;
	private String identificationDocument;
	private String driverLicenseNumber;
	private static transient AppConfig conf = new AppConfig();

	public User() {
		super();
	}

	public User(String name, String identificationDocument, String driverLicenseNumber) {
		super();
		this.name = name;
		this.identificationDocument = identificationDocument;
		this.driverLicenseNumber = driverLicenseNumber;
	}

	public User(String name) {
		super();
		this.name = name;
	}

	public void generateDocumentation() {
		identificationDocument = Util.generateUUID(conf.gerUserDocLength());
		driverLicenseNumber = Util.generateUUID(conf.gerUserDocLength());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("User: [name=%s, identificationDocument=%s, driverLicenseNumber=%s]",
				name != null ? name : "", identificationDocument != null ? identificationDocument : "none",
				driverLicenseNumber != null ? driverLicenseNumber : "none");
	}

}
