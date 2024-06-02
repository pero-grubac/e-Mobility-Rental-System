package net.etfbl.pj2.model;

import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

/**
 * Represents a user with personal information and documentation.
 * 
 *  @author Pero Grubač
 * @since 2.6.2024.
 */
public class User {

	private String name;
	private String identificationDocument;
	private String driverLicenseNumber;
	private static transient AppConfig conf = new AppConfig();

	/**
	 * Constructs a new User object with default values.
	 */
	public User() {
		super();
	}

	/**
	 * Constructs a new User object with the specified parameters.
	 * 
	 * @param name                   The name of the user.
	 * @param identificationDocument The identification document of the user.
	 * @param driverLicenseNumber    The driver license number of the user.
	 */
	public User(String name, String identificationDocument, String driverLicenseNumber) {
		super();
		this.name = name;
		this.identificationDocument = identificationDocument;
		this.driverLicenseNumber = driverLicenseNumber;
	}

	/**
	 * Constructs a new User object with the specified name.
	 * 
	 * @param name The name of the user.
	 */
	public User(String name) {
		super();
		this.name = name;
	}

	/**
	 * Generates documentation for the user, including identification document and
	 * driver license number.
	 */
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
