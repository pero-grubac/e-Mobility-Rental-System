package net.etfbl.pj2.model;

import java.util.Random;

import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

/**
 * A person who rents vehicles. Users who rent a car must show an
 * identification document (a passport for foreign citizens, an ID card for
 * domestic ones) and a driver's license.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class User {

	/** The kind of identification document a user shows. */
	public enum DocumentType {
		/** ID card of a domestic citizen. */
		ID_CARD,
		/** Passport of a foreign citizen. */
		PASSPORT
	}

	private static final Random RANDOM = new Random();

	private final String name;
	private DocumentType documentType;
	private String identificationDocument;
	private String driverLicenseNumber;

	/**
	 * Constructs a new user without documents.
	 *
	 * @param name The name of the user.
	 */
	public User(String name) {
		this.name = name;
	}

	/**
	 * Issues an identification document and a driver's license. A share of the
	 * users, set in the configuration, are foreign citizens and get a passport.
	 *
	 * @param conf The application configuration.
	 */
	public void issueDocuments(AppConfig conf) {
		documentType = RANDOM.nextDouble() < conf.getForeignUserShare() ? DocumentType.PASSPORT
				: DocumentType.ID_CARD;
		identificationDocument = Util.generateId(conf.getUserDocumentLength());
		driverLicenseNumber = Util.generateId(conf.getUserDocumentLength());
	}

	/**
	 * @return True if the user has shown documents.
	 */
	public boolean hasDocuments() {
		return identificationDocument != null;
	}

	public String getName() {
		return name;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public String getIdentificationDocument() {
		return identificationDocument;
	}

	public String getDriverLicenseNumber() {
		return driverLicenseNumber;
	}

	@Override
	public String toString() {
		return name;
	}
}
