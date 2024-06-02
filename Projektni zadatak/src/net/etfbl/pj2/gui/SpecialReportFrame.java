package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.TableView;

import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.SpecialReport;

/**
 * Frame for displaying special reports, extends {@link GeneralFrame}. It
 * creates a table based on the provided special report data.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SpecialReportFrame extends GeneralFrame {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private Integer columnLength = 0;

	/**
	 * Constructs a new SpecialReportFrame with the specified configuration, special
	 * report data, and title.
	 *
	 * @param conf   The application configuration.
	 * @param report The special report data.
	 * @param title  The title of the frame.
	 */
	public SpecialReportFrame(AppConfig conf, SpecialReport report, String title) {
		super(conf, title);

		createTable(conf, report);
		setPreferredSizeBasedOnContent(table);

		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	/**
	 * Creates a table to display the special report data.
	 *
	 * @param conf   The application configuration.
	 * @param report The special report data.
	 */
	private void createTable(AppConfig conf, SpecialReport report) {

		try {
			String[] columnNames = { conf.getColumnType(), conf.getColumnId(), conf.getColumnManufacturer(),
					conf.getColumnModel(), conf.getColumnPurchasePrice(), conf.getColumnBatteryLevel(),
					conf.getColumnRevenue(), conf.getColumnMaxSpeed(), conf.getColumnRamgePerCharge(),
					conf.getColumnPurchaseDate(), conf.getColumnDescription() };

			DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			for (int i = 0; i < columnNames.length; i++)
				if (columnLength < columnNames[i].length())
					columnLength = columnNames[i].length();

			report.getTopVehiclesByIncome().forEach((vehicle, revenue) -> {
				String type = "";
				String maxSpeed = "";
				String rangePerCharge = "";
				String purchaseDate = "";
				String description = "";
				if (vehicle instanceof Car) {
					type = conf.getCarName();
					purchaseDate = ((Car) vehicle).getPurchaseDate().format(TransportVehicle.getDateFormatter());
					description = ((Car) vehicle).getDescription();
				} else if (vehicle instanceof ElectricBike) {
					type = conf.getBikeName();
					rangePerCharge = ((ElectricBike) vehicle).getRangePerCharge().toString();
				} else if (vehicle instanceof ElectricScooter) {
					type = conf.getScooterName();
					maxSpeed = ((ElectricScooter) vehicle).getMaxSpeed().toString();
				} else
					type = "Error";

				Object[] rowData = new Object[] { type, vehicle.getId(), vehicle.getManufacturer(), vehicle.getModel(),
						vehicle.getPurchasePrice(), vehicle.getBatteryLevel() + "%", revenue, maxSpeed, rangePerCharge,
						purchaseDate, description };
				for (int i = 0; i < rowData.length; i++)
					if (columnLength < rowData[i].toString().length())
						columnLength = rowData[i].toString().length();
				model.addRow(rowData);

			});

			table = new JTable(model);
			table.setAutoCreateRowSorter(true);

			resizeColumnWidth(table);

			table.getTableHeader().setReorderingAllowed(false);

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			}
			JScrollPane scrollPane = new JScrollPane(table);
			add(scrollPane, BorderLayout.CENTER);
		} catch (Exception e) {
			System.err.println("Error " + e.getMessage());
		}
	}

}
