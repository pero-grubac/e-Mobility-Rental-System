package net.etfbl.pj2.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

/**
 * A small rounded square in one color, used in legends and titles.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class DotIcon implements Icon {
	private final Color color;
	private final int size;
	private final Color border;

	/**
	 * @param color The fill color.
	 * @param size  The width and height.
	 */
	public DotIcon(Color color, int size) {
		this(color, size, null);
	}

	/**
	 * @param color  The fill color.
	 * @param size   The width and height.
	 * @param border The border color, or null for none.
	 */
	public DotIcon(Color color, int size, Color border) {
		this.color = color;
		this.size = size;
		this.border = border;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color);
		g2.fillRoundRect(x, y, size, size, size / 2, size / 2);
		if (border != null) {
			g2.setColor(border);
			g2.drawRoundRect(x, y, size - 1, size - 1, size / 2, size / 2);
		}
		g2.dispose();
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}
}
