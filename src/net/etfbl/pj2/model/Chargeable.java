package net.etfbl.pj2.model;

/**
 * An interface representing objects that can be charged and drained. Classes
 * implementing this interface are expected to provide implementations for
 * charging and draining their batteries or energy sources.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public interface Chargeable {

	/**
	 * Charges the battery or energy source associated with the implementing object.
	 */
	public void chargeBattery();

	/**
	 * Drains the battery or energy source associated with the implementing object.
	 */
	public void drainBattery();

}
