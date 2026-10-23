package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import net.etfbl.pj2.resources.AppConfig;

/**
 * Base window of the application: title, icon, background, a header with a
 * title and a hint, and helpers for tables that refresh themselves.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class GeneralFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private final AppConfig conf;
	private final JLabel hint;
	private Timer refreshTimer;

	/**
	 * Creates a window with a header.
	 *
	 * @param conf  The application configuration.
	 * @param title The title of the window, or null for the main window.
	 */
	public GeneralFrame(AppConfig conf, String title) {
		this.conf = conf;
		setTitle(title == null ? conf.getText("APP_TITLE") : conf.getText("APP_TITLE") + "  ·  " + title);
		setIconImage(new ImageIcon(conf.getIconPath("APP_ICON")).getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(conf.getColor("BACKGROUND_COLOR"));
		getContentPane().setLayout(new BorderLayout(0, 12));
		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));
		hint = Theme.label(" ", Font.PLAIN, 13f, conf.getColor("MUTED_TEXT_COLOR"));
		if (title != null) {
			JPanel header = new JPanel();
			header.setOpaque(false);
			header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
			header.add(Theme.label(title, Font.BOLD, 20f, conf.getColor("TEXT_COLOR")));
			header.add(hint);
			add(header, BorderLayout.NORTH);
		}
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (refreshTimer != null)
					refreshTimer.stop();
			}
		});
	}

	public AppConfig getConf() {
		return conf;
	}

	/**
	 * @param text The text under the title of the window.
	 */
	protected void setHint(String text) {
		hint.setText(text);
	}

	/**
	 * Runs the task now and then again every refresh period, while the window is
	 * open.
	 *
	 * @param task The task, run on the Swing thread.
	 */
	protected void refreshEverySecond(Runnable task) {
		task.run();
		refreshTimer = new Timer(conf.getScreenRefreshMillis(), e -> task.run());
		refreshTimer.start();
	}

	/**
	 * Creates a read-only, styled table.
	 *
	 * @param columns The column names.
	 * @return The table.
	 */
	protected JTable createTable(String... columns) {
		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable table = new JTable(model);
		Theme.styleTable(table);
		return table;
	}

	/**
	 * Replaces the rows of a table created by {@link #createTable(String...)}.
	 *
	 * @param table The table.
	 * @param rows  The new rows.
	 */
	protected static void setRows(JTable table, List<Object[]> rows) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		boolean same = model.getRowCount() == rows.size();
		for (int r = 0; same && r < rows.size(); r++) {
			for (int c = 0; c < model.getColumnCount(); c++) {
				if (!java.util.Objects.equals(model.getValueAt(r, c), rows.get(r)[c])) {
					model.setValueAt(rows.get(r)[c], r, c);
				}
			}
		}
		if (!same) {
			model.setRowCount(0);
			rows.forEach(model::addRow);
		}
	}

	/**
	 * Puts a table in a card that is as tall as its rows.
	 *
	 * @param table   The table.
	 * @param minRows The number of rows the card shows at least.
	 * @return The card.
	 */
	protected JPanel tableCard(JTable table, int minRows) {
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		int rows = Math.max(minRows, table.getRowCount());
		scroll.setPreferredSize(new Dimension(200, table.getTableHeader().getPreferredSize().height
				+ rows * table.getRowHeight() + 4));
		RoundedPanel card = new RoundedPanel(new BorderLayout(), 6);
		card.add(scroll, BorderLayout.CENTER);
		return card;
	}

	/**
	 * Creates a small section title with a colored marker.
	 *
	 * @param text  The title.
	 * @param color The marker color.
	 * @return The title label.
	 */
	protected JLabel sectionTitle(String text, Color color) {
		JLabel label = Theme.label(text, Font.BOLD, 15f, conf.getColor("TEXT_COLOR"));
		label.setIcon(new DotIcon(color, 12));
		label.setIconTextGap(8);
		label.setBorder(BorderFactory.createEmptyBorder(10, 2, 6, 0));
		return label;
	}
}
