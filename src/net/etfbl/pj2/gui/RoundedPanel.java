package net.etfbl.pj2.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A white card with rounded corners and a thin border, used to group content.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class RoundedPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int ARC = 16;
	private static final Color BORDER = new Color(0xE2E8F0);

	/**
	 * @param layout  The layout of the card.
	 * @param padding The space between the edge and the content.
	 */
	public RoundedPanel(LayoutManager layout, int padding) {
		super(layout);
		setOpaque(false);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
		g2.setColor(BORDER);
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
		g2.dispose();
		super.paintComponent(g);
	}
}
