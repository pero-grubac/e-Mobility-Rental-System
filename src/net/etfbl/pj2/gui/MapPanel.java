package net.etfbl.pj2.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

/**
 * The shared city map. Fields of the narrow and the wide part of the city have
 * their own colors; a field with a vehicle on it takes the color of the vehicle
 * type and shows the vehicle ID and its battery level. The rest of every
 * active route is shown faintly, with its destination outlined.
 * <p>
 * Must only be used from the Swing thread.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int AXIS = 22;
	private static final int GAP = 3;

	private final AppConfig conf;
	private final int columns;
	private final int rows;
	private final Color narrowColor;
	private final Color wideColor;
	private final Color gridColor;
	private final Color mutedColor;
	private final boolean[][] narrow;
	private final Map<Invoice, Field> positions = new LinkedHashMap<>();

	/**
	 * @param conf The application configuration.
	 */
	public MapPanel(AppConfig conf) {
		this.conf = conf;
		this.columns = conf.getMapXMax() - conf.getMapXMin() + 1;
		this.rows = conf.getMapYMax() - conf.getMapYMin() + 1;
		this.narrowColor = conf.getColor("NARROW_AREA_COLOR");
		this.wideColor = conf.getColor("WIDE_AREA_COLOR");
		this.gridColor = conf.getColor("MAP_GRID_COLOR");
		this.mutedColor = conf.getColor("MUTED_TEXT_COLOR");
		this.narrow = new boolean[rows][columns];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				narrow[r][c] = conf.isNarrowArea(new Field(conf.getMapXMin() + c, conf.getMapYMin() + r));
			}
		}
		setOpaque(false);
		setPreferredSize(new Dimension(760, 760));
	}

	/**
	 * Shows the vehicle of a rental on a field.
	 *
	 * @param invoice The invoice of the rental.
	 * @param field   The field.
	 */
	public void moveVehicle(Invoice invoice, Field field) {
		positions.put(invoice, field);
		repaint();
	}

	/**
	 * Removes the vehicle of a finished rental from the map.
	 *
	 * @param invoice The invoice of the rental.
	 */
	public void removeVehicle(Invoice invoice) {
		positions.remove(invoice);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int cell = Math.max(8, Math.min((getWidth() - AXIS) / columns, (getHeight() - AXIS) / rows));
		int left = AXIS + (getWidth() - AXIS - cell * columns) / 2;
		int top = AXIS + (getHeight() - AXIS - cell * rows) / 2;

		paintAxes(g2, cell, left, top);
		paintFields(g2, cell, left, top);
		paintRoutes(g2, cell, left, top);
		paintVehicles(g2, cell, left, top);
		g2.dispose();
	}

	private void paintAxes(Graphics2D g2, int cell, int left, int top) {
		g2.setFont(Theme.font(Font.PLAIN, Math.max(9f, cell * 0.24f)));
		g2.setColor(mutedColor);
		FontMetrics fm = g2.getFontMetrics();
		for (int c = 0; c < columns; c++) {
			String text = String.valueOf(conf.getMapXMin() + c);
			g2.drawString(text, left + c * cell + (cell - fm.stringWidth(text)) / 2, top - 7);
		}
		for (int r = 0; r < rows; r++) {
			String text = String.valueOf(conf.getMapYMin() + r);
			g2.drawString(text, left - fm.stringWidth(text) - 7, top + r * cell + (cell + fm.getAscent()) / 2 - 2);
		}
	}

	private void paintFields(Graphics2D g2, int cell, int left, int top) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				int x = left + c * cell + GAP / 2;
				int y = top + r * cell + GAP / 2;
				int size = cell - GAP;
				g2.setColor(narrow[r][c] ? narrowColor : wideColor);
				g2.fillRoundRect(x, y, size, size, 7, 7);
				g2.setColor(gridColor);
				g2.drawRoundRect(x, y, size, size, 7, 7);
			}
		}
	}

	private void paintRoutes(Graphics2D g2, int cell, int left, int top) {
		for (Map.Entry<Invoice, Field> entry : positions.entrySet()) {
			List<Field> route = entry.getKey().getRental().getRoute();
			Color color = Theme.vehicleColor(conf, entry.getKey().getVehicle().getType());
			int from = route.indexOf(entry.getValue());
			g2.setColor(Theme.withAlpha(color, 45));
			for (int i = from + 1; i < route.size(); i++) {
				int[] p = position(route.get(i), cell, left, top);
				g2.fillRoundRect(p[0] + 3, p[1] + 3, cell - GAP - 6, cell - GAP - 6, 6, 6);
			}
			int[] end = position(route.get(route.size() - 1), cell, left, top);
			g2.setColor(Theme.withAlpha(color, 170));
			g2.setStroke(new BasicStroke(2f));
			g2.drawRoundRect(end[0] + 1, end[1] + 1, cell - GAP - 2, cell - GAP - 2, 8, 8);
		}
	}

	private void paintVehicles(Graphics2D g2, int cell, int left, int top) {
		Map<Field, List<TransportVehicle>> byField = new LinkedHashMap<>();
		positions.forEach((invoice, field) -> byField.computeIfAbsent(field, f -> new ArrayList<>())
				.add(invoice.getVehicle()));

		Font idFont = Theme.font(Font.BOLD, Math.max(9f, cell * 0.31f));
		Font batteryFont = Theme.font(Font.PLAIN, Math.max(8f, cell * 0.25f));
		int size = cell - GAP;
		for (Map.Entry<Field, List<TransportVehicle>> entry : byField.entrySet()) {
			TransportVehicle vehicle = entry.getValue().get(0);
			int[] p = position(entry.getKey(), cell, left, top);
			g2.setColor(Theme.vehicleColor(conf, vehicle.getType()));
			g2.fillRoundRect(p[0], p[1], size, size, 9, 9);

			String id = vehicle.getId() + (entry.getValue().size() > 1 ? "+" + (entry.getValue().size() - 1) : "");
			g2.setColor(Color.WHITE);
			g2.setFont(idFont);
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(id, p[0] + (size - fm.stringWidth(id)) / 2, p[1] + size / 2 - 1);

			String battery = vehicle.getBatteryText();
			g2.setFont(batteryFont);
			fm = g2.getFontMetrics();
			g2.setColor(Theme.withAlpha(Color.WHITE, 230));
			g2.drawString(battery, p[0] + (size - fm.stringWidth(battery)) / 2, p[1] + size / 2 + fm.getAscent());

			int barWidth = size - 10;
			int level = (int) Math.round(barWidth * vehicle.getBatteryLevel() / conf.getBatteryMaxLevel());
			g2.setColor(Theme.withAlpha(Color.WHITE, 70));
			g2.fillRoundRect(p[0] + 5, p[1] + size - 6, barWidth, 3, 3, 3);
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(p[0] + 5, p[1] + size - 6, Math.max(1, level), 3, 3, 3);
		}
	}

	private int[] position(Field field, int cell, int left, int top) {
		return new int[] { left + (field.getX() - conf.getMapXMin()) * cell + GAP / 2,
				top + (field.getY() - conf.getMapYMin()) * cell + GAP / 2 };
	}
}
