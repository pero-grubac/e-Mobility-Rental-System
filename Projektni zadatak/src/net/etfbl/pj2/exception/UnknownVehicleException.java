package net.etfbl.pj2.exception;

/**
 * Exception thrown when an unknown vehicle type is encountered. This class
 * provides multiple constructors to handle different exception scenarios.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class UnknownVehicleException extends Exception {
	/**
	 * Constructs a new UnknownVehicleException with {@code null} as its detail
	 * message.
	 */
	public UnknownVehicleException() {
		super();
	}

	/**
	 * Constructs a new UnknownVehicleException with the specified detail message.
	 * 
	 * @param message The detail message. The detail message is saved for later
	 *                retrieval by the {@link Throwable#getMessage()} method.
	 */
	public UnknownVehicleException(String message) {
		super("Unknown vehicle type: " + message);
	}

	/**
	 * Constructs a new UnknownVehicleException with the specified cause.
	 * 
	 * @param cause The cause (which is saved for later retrieval by the
	 *              {@link Throwable#getCause()} method). (A {@code null} value is
	 *              permitted, and indicates that the cause is nonexistent or
	 *              unknown.)
	 */
	public UnknownVehicleException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new UnknownVehicleException with the specified detail message
	 * and cause.
	 * 
	 * @param message The detail message (which is saved for later retrieval by the
	 *                {@link Throwable#getMessage()} method).
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                {@link Throwable#getCause()} method). (A {@code null} value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 */
	public UnknownVehicleException(String message, Throwable cause) {
		super("Unknown vehicle type: " + message, cause);
	}
}
