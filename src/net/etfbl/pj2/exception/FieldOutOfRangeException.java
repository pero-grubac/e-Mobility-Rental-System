package net.etfbl.pj2.exception;

/**
 * Exception thrown when a field is out of the allowed range. This class
 * provides multiple constructors to handle different exception scenarios.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class FieldOutOfRangeException extends Exception {
	/**
	 * Constructs a new FieldOutOfRangeException with {@code null} as its detail
	 * message.
	 */
	public FieldOutOfRangeException() {
		super();
	}

	/**
	 * Constructs a new FieldOutOfRangeException with the specified detail message.
	 * 
	 * @param message The detail message. The detail message is saved for later
	 *                retrieval by the {@link Throwable#getMessage()} method.
	 */
	public FieldOutOfRangeException(String message) {
		super("Field out of range: " + message);
	}

	/**
	 * Constructs a new FieldOutOfRangeException with the specified cause.
	 * 
	 * @param cause The cause (which is saved for later retrieval by the
	 *              {@link Throwable#getCause()} method). (A {@code null} value is
	 *              permitted, and indicates that the cause is nonexistent or
	 *              unknown.)
	 */
	public FieldOutOfRangeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new FieldOutOfRangeException with the specified detail message
	 * and cause.
	 * 
	 * @param message The detail message (which is saved for later retrieval by the
	 *                {@link Throwable#getMessage()} method).
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                {@link Throwable#getCause()} method). (A {@code null} value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 */
	public FieldOutOfRangeException(String message, Throwable cause) {
		super("Field out of range: " + message, cause);
	}
}
