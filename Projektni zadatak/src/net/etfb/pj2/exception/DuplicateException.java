package net.etfb.pj2.exception;

public class DuplicateException extends Exception {
	public DuplicateException() {
		super();
	}

	public DuplicateException(String message) {
		super("Duplicate error: " + message);
	}

	public DuplicateException(Throwable cause) {
		super(cause);
	}

	public DuplicateException(String message, Throwable cause) {
		super("Duplicate error: " + message, cause);
	}
}
