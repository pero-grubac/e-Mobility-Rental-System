package net.etfbl.pj2.exception;

/**
 * Exception thrown when an error occurs during parsing. This class provides
 * multiple constructors to handle different exception scenarios.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class ParsingException extends Exception {
	/**
	 * Constructs a new ParsingException with {@code null} as its detail message.
	 */
	public ParsingException() {
		super();
	}

	/**
	 * Constructs a new ParsingException with the specified detail message.
	 * 
	 * @param message The detail message. The detail message is saved for later
	 *                retrieval by the {@link Throwable#getMessage()} method.
	 */
	public ParsingException(String message) {
		super("Parsing error: " + message);
	}

	/**
	 * Constructs a new ParsingException with the specified cause.
	 * 
	 * @param cause The cause (which is saved for later retrieval by the
	 *              {@link Throwable#getCause()} method). (A {@code null} value is
	 *              permitted, and indicates that the cause is nonexistent or
	 *              unknown.)
	 */
	public ParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new ParsingException with the specified detail message and
	 * cause.
	 * 
	 * @param message The detail message (which is saved for later retrieval by the
	 *                {@link Throwable#getMessage()} method).
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                {@link Throwable#getCause()} method). (A {@code null} value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 */
	public ParsingException(String message, Throwable cause) {
		super("Parsing error: " + message, cause);
	}
}
