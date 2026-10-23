package net.etfbl.pj2.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Look and feel, colors, fonts and formatting shared by all windows.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public final class Theme {
	private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));

	private Theme() {
	}

	/**
	 * Installs the look and feel set in the configuration, or the system one if
	 * it is not available, and the shared component styles.
	 *
	 * @param conf The application configuration.
	 */
	public static void install(AppConfig conf) {
		try {
			UIManager.setLookAndFeel(conf.getLookAndFeel());
		} catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
				// the default look and feel stays
			}
		}
		Color accent = conf.getColor("ACCENT_COLOR");
		UIManager.put("Component.arc", 10);
		UIManager.put("Button.arc", 10);
		UIManager.put("ProgressBar.arc", 10);
		UIManager.put("Component.focusWidth", 1);
		UIManager.put("ProgressBar.foreground", accent);
		UIManager.put("Table.rowHeight", 30);
		UIManager.put("Table.showHorizontalLines", true);
		UIManager.put("Table.showVerticalLines", false);
		UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
		UIManager.put("Table.alternateRowColor", new Color(0xF8FAFC));
		UIManager.put("Table.selectionBackground", new Color(0xDBEAFE));
		UIManager.put("Table.selectionForeground", conf.getColor("TEXT_COLOR"));
		UIManager.put("TableHeader.height", 34);
		UIManager.put("TableHeader.background", new Color(0xF1F5F9));
		UIManager.put("TitlePane.unifiedBackground", true);
		UIManager.put("ScrollBar.thumbArc", 999);
		UIManager.put("ScrollBar.width", 10);
	}

	/**
	 * @param style The font style, for example {@link Font#BOLD}.
	 * @param size  The font size.
	 * @return The default font of the look and feel in that style and size.
	 */
	public static Font font(int style, float size) {
		Font base = UIManager.getFont("Label.font");
		if (base == null)
			base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
		return base.deriveFont(style, size);
	}

	/**
	 * @param conf The application configuration.
	 * @param type A vehicle type.
	 * @return The color of the type.
	 */
	public static Color vehicleColor(AppConfig conf, VehicleType type) {
		return Color.decode(conf.getVehicleColor(type));
	}

	/**
	 * @param color A color.
	 * @param alpha The opacity, from 0 to 255.
	 * @return The same color with the given opacity.
	 */
	public static Color withAlpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	/**
	 * @param value An amount of money.
	 * @return The amount with thousands separators and two decimals.
	 */
	public static String money(BigDecimal value) {
		synchronized (MONEY) {
			return MONEY.format(value);
		}
	}

	/**
	 * @param value A number.
	 * @return The number with thousands separators and two decimals.
	 */
	public static String money(double value) {
		return money(BigDecimal.valueOf(value));
	}

	/**
	 * Creates a label in the given font and color.
	 *
	 * @param text  The text.
	 * @param style The font style.
	 * @param size  The font size.
	 * @param color The text color.
	 * @return The label.
	 */
	public static JLabel label(String text, int style, float size, Color color) {
		JLabel label = new JLabel(text);
		label.setFont(font(style, size));
		label.setForeground(color);
		return label;
	}

	/**
	 * Applies the shared table style: roomy rows, light horizontal lines,
	 * centered values, money with two decimals, sortable columns and a fixed
	 * column order.
	 *
	 * @param table The table.
	 */
	public static void styleTable(JTable table) {
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		table.setRowHeight(30);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(true);
		table.setGridColor(new Color(0xE2E8F0));
		table.setIntercellSpacing(new Dimension(0, 1));
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setFont(font(Font.BOLD, 12.5f));
		if (table.getTableHeader().getDefaultRenderer() instanceof JLabel header) {
			header.setHorizontalAlignment(SwingConstants.CENTER);
		}
		table.setDefaultRenderer(Object.class, new CellRenderer());
		table.setBorder(BorderFactory.createEmptyBorder());
	}

	/** Centers every value, shows money with two decimals and negative money in red. */
	private static final class CellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Object shown = value instanceof BigDecimal ? money((BigDecimal) value) : value;
			super.getTableCellRendererComponent(table, shown, isSelected, false, row, column);
			setHorizontalAlignment(SwingConstants.CENTER);
			setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			if (value instanceof BigDecimal && ((BigDecimal) value).signum() < 0 && !isSelected) {
				setForeground(new Color(0xDC2626));
			} else if (!isSelected) {
				setForeground(table.getForeground());
			}
			return this;
		}
	}
}
