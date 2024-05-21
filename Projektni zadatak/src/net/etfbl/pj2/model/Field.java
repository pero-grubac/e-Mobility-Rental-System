package net.etfbl.pj2.model;

public class Field {
	private Integer x;
	private Integer y;

	public Field(Integer x, Integer y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
