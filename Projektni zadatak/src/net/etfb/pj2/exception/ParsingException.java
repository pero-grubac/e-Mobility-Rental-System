package net.etfb.pj2.exception;

public class ParsingException extends Exception {
	public ParsingException() {
		super();
	}

	public ParsingException(String message) {
		super("Parsing error: "+message);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}

	public ParsingException(String message, Throwable cause) {
		super("Parsing error: " + message, cause);
	}
}
