package net.etfbl.pj2.exception;

public class FieldOutOfRangeException extends Exception {
	public FieldOutOfRangeException() {
		super();
	}

	public FieldOutOfRangeException(String message) {
		super("Field out of range: " + message);
	}

	public FieldOutOfRangeException(Throwable cause) {
		super(cause);
	}

	public FieldOutOfRangeException(String message, Throwable cause) {
		super("Field out of range: " + message, cause);
	}
}
