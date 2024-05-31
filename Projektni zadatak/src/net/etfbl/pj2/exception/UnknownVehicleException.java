package net.etfbl.pj2.exception;

public class UnknownVehicleException extends Exception {
	
	public UnknownVehicleException() {
		super();
	}

	public UnknownVehicleException(String message) {
		super("Unknown vehicle type: " + message);
	}

	public UnknownVehicleException(Throwable cause) {
		super(cause);
	}

	public UnknownVehicleException(String message, Throwable cause) {
		super("Unknown vehicle type: " + message, cause);
	}
}
