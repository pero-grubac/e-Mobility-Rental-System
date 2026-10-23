package net.etfbl.pj2.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * A field of the city map. Field (0,0) is the top left one; x grows to the
 * right and y grows downwards.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public final class Field implements Serializable {
	private static final long serialVersionUID = 1L;

	private final int x;
	private final int y;

	/**
	 * Constructs a new field with the specified coordinates.
	 *
	 * @param x The x-coordinate (column) of the field.
	 * @param y The y-coordinate (row) of the field.
	 */
	public Field(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Field))
			return false;
		Field other = (Field) o;
		return x == other.x && y == other.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
