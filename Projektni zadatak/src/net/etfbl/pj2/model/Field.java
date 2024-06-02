package net.etfbl.pj2.model;

/**
 * Represents a field on the map. Each field has an x and y coordinate.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class Field {
	private Integer x;
	private Integer y;

	/**
	 * Constructs a new Field object with the specified x and y coordinates.
	 * 
	 * @param x The x-coordinate of the field.
	 * @param y The y-coordinate of the field.
	 */
	public Field(Integer x, Integer y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
