package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.SummaryReport;

/**
 * A frame for displaying reports, extends {@link GeneralFrame}. It creates a
 * table based on the provided report data.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class ReportFrame extends GeneralFrame {
	private static final long serialVersionUID = 1L;
	private JTable table;

	/**
	 * Constructs a new ReportFrame with the specified configuration, report data,
	 * and date.
	 *
	 * @param conf   The application configuration.
	 * @param report The daily report data.
	 * @param date   The date for the report.
	 */
	public ReportFrame(AppConfig conf, DailyReport report, String date) {
		super(conf, date);
		try {
			createTable(conf, report);
			setPreferredSizeBasedOnContent(table);
			pack();
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Creates a table to display the report data.
	 *
	 * @param conf   The application configuration.
	 * @param report The daily report data.
	 */
	private void createTable(AppConfig conf, DailyReport report) {

		List<String> columnNamesList = new ArrayList<>(Arrays.asList(conf.getColumnTotalIncome(),
				conf.getColumnTotalDiscount(), conf.getColumnTotalPromotion(), conf.getColumnnNarrowAreaIncome(),
				conf.getColumnWideAreaIncome(), conf.getColumnMaintenance(), conf.getColumnRepairs()));

		List<Object> rowDataList = new ArrayList<>(Arrays.asList(report.getTotalIncome().toString(),
				report.getTotalDiscount().toString(), report.getTotalPromotions().toString(),
				report.getTotalIncomeInWideArea().toString(), report.getTotalIncomeInNarrowArea().toString(),
				report.getTotalAmountForMaintenance().toString(), report.getTotalAmountForRepairs().toString()));

		if (report instanceof SummaryReport) {
			columnNamesList.add(conf.getColumnTax());
			columnNamesList.add(conf.getColumnCost());
			rowDataList.add(((SummaryReport) report).getTotalTax().toString());
			rowDataList.add(((SummaryReport) report).getTotalCost().toString());
		}

		String[] columnNames = columnNamesList.toArray(new String[0]);
		Object[] rowData = rowDataList.toArray(new Object[0]);

		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(model);
		table.setAutoCreateRowSorter(true);

		model.addRow(rowData);

		table.getTableHeader().setReorderingAllowed(false);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

}
