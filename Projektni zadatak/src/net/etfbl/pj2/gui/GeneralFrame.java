package net.etfbl.pj2.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.etfbl.pj2.resources.AppConfig;

/**
 * A general frame for the application, extending {@link JFrame}. It sets up the
 * frame properties based on the given configuration.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class GeneralFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private AppConfig conf;

	/**
	 * Constructs a new GeneralFrame with the specified configuration and title.
	 * 
	 * @param conf  The application configuration.
	 * @param title The title for the frame.
	 */
	public GeneralFrame(AppConfig conf, String title) {
		this.conf = conf;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		try {
			String fullTitle = conf.getAppTitle() + " " + title;
			setTitle(fullTitle);
			setIconImage(new ImageIcon(conf.getIconsFolder() + File.separator + conf.getFrameIcon()).getImage());
		} catch (Exception e) {
			System.err.println("Error " + e.getMessage());
		}
		setSize(new Dimension(100, 100));
		setLocationRelativeTo(null);
	}

	public AppConfig getConf() {
		return conf;
	}

	/**
	 * Resizes the column width of the given table to fit the content.
	 * 
	 * @param table The table whose column width needs to be resized.
	 */
	protected void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		int maxColumnWidth = 15;

		for (int column = 0; column < table.getColumnCount(); column++) {
			TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table,
					table.getColumnModel().getColumn(column).getHeaderValue(), false, false, 0, column);
			maxColumnWidth = Math.max(maxColumnWidth, headerComp.getPreferredSize().width + 1);

			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				maxColumnWidth = Math.max(maxColumnWidth, comp.getPreferredSize().width + 1);
			}
		}

		for (int column = 0; column < table.getColumnCount(); column++) {
			columnModel.getColumn(column).setPreferredWidth(maxColumnWidth);
		}
	}

	/**
	 * Returns the preferred width of the given table based on its content.
	 * 
	 * @param table The table whose preferred width is to be calculated.
	 * @return The preferred width of the table.
	 */
	protected int getPreferredTableWidth(JTable table) {
		return table.getColumnModel().getColumn(0).getPreferredWidth() * table.getColumnCount();
	}

	/**
	 * Sets the preferred size of the table based on its content.
	 * 
	 * @param table The table whose preferred size is to be set.
	 */
	protected void setPreferredSizeBasedOnContent(JTable table) {
		int tableWidth = getPreferredTableWidth(table);
		int tableHeight = table.getRowHeight() * table.getRowCount();
		table.setPreferredScrollableViewportSize(new Dimension(tableWidth + 44, tableHeight));
	}
}
