package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.DailyReportParser;
import net.etfbl.pj2.parser.SummaryReportParser;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.statistics.SpecialReport;
import net.etfbl.pj2.statistics.SummaryReport;
import net.etfbl.pj2.util.Util;

/**
 * Main frame of the application that extends {@link GeneralFrame}. It sets up
 * the main user interface including menu bar, board, and vehicle
 * initialization.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class MainFrame extends GeneralFrame {

	private static final long serialVersionUID = 1L;

	private JPanel pnlDate;
	private JLabel lblDate;
	private JPanel pnlTime;
	private JLabel lblTime;
	private JPanel pnlNorth;

	private JMenuBar menuBar;
	private JMenu menuReport;
	private JMenuItem miDailyReport;
	private JMenuItem miSummaryReport;
	private JMenuItem miSpecialReport;

	private JPanel pnlMain;
	private JPanel[][] pnlBoard;
	private JLabel[][] lblVehicle;
	private List<TransportVehicle>[][] vehicles;

	private Color carColor;
	private Color bikeColor;
	private Color scooterColor;
	private Color wideColor;
	private Color narrowColor;

	private List<Invoice> invoices;
	private LocalDate currentDate;

	/**
	 * Constructs a new MainFrame with the specified configuration and list of
	 * invoices.
	 *
	 * @param conf     The application configuration.
	 * @param invoices The list of invoices.
	 */
	public MainFrame(AppConfig conf, List<Invoice> invoices) {
		super(conf, "");
		try {
			this.invoices = invoices;
			this.currentDate = LocalDate.now();
			this.carColor = Color.decode(conf.getCarColor());
			this.bikeColor = Color.decode(conf.getBikeColor());
			this.scooterColor = Color.decode(conf.getScooterColor());
			this.wideColor = Color.decode(conf.getWideAreaColor());
			this.narrowColor = Color.decode(conf.getNarrowColor());
			configureFrame();
			createMenuBar();
			createBoard();
			initializeVehicles();
		} catch (Exception e) {
			System.err.println("Error " + e.getMessage());
		}
	}

	/**
	 * Configures the frame by setting up date and time panels and their layout.
	 */
	private void configureFrame() {

		pnlDate = createLabelPanel(getConf().getLabelDate(), lblDate = new JLabel());
		pnlTime = createLabelPanel(getConf().getLabelTime(), lblTime = new JLabel());

		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(pnlDate, BorderLayout.NORTH);
		pnlNorth.add(pnlTime, BorderLayout.SOUTH);
		pnlNorth.setSize(getWidth(), 3);

		setLayout(new BorderLayout());
		add(pnlNorth, BorderLayout.NORTH);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Creates a panel with a label and sets its background color.
	 *
	 * @param labelText The text for the label.
	 * @param label     The label to be added to the panel.
	 * @return The created panel.
	 */
	private JPanel createLabelPanel(String labelText, JLabel label) {
		JPanel panel = new JPanel();
		label.setText(labelText);
		panel.add(label);
		panel.setBackground(Color.decode(getConf().getBackgroundColor()));
		return panel;
	}

	/**
	 * Creates the menu bar with report options and their action listeners.
	 */
	private void createMenuBar() {
		menuBar = new JMenuBar();
		menuReport = new JMenu(getConf().getMenuReport());
		menuBar.add(menuReport);

		miDailyReport = new JMenuItem(getConf().getDailyReportName());
		miSummaryReport = new JMenuItem(getConf().getSummaryReportName());
		miSpecialReport = new JMenuItem(getConf().getSpecialReportName());
		menuReport.add(miDailyReport);
		menuReport.add(miSummaryReport);
		menuReport.add(miSpecialReport);

		String dailyReportImagePath = getConf().getIconsFolder() + File.separator + getConf().getDailyReportIcon();
		String summaryReportImagePath = getConf().getIconsFolder() + File.separator + getConf().getSummaryReportIcon();
		String specialReportImagePath = getConf().getIconsFolder() + File.separator + getConf().getSpecialReportIcon();

		ImageIcon dailyReportImage = new ImageIcon(dailyReportImagePath);
		ImageIcon summaryReportImage = new ImageIcon(summaryReportImagePath);
		ImageIcon specialReportImage = new ImageIcon(specialReportImagePath);

		miDailyReport.setIcon(dailyReportImage);
		miDailyReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DailyReport dailyReport = new DailyReport(invoices);
				DailyReportParser dailyReportParser = new DailyReportParser();
				Map<String, DailyReport> dailyReports = dailyReportParser.parseDailyReport();
				dailyReports.forEach((date, report) -> {
					ReportFrame reportFrame = new ReportFrame(getConf(), dailyReport, date);
					reportFrame.setVisible(true);
				});

			}
		});

		miSummaryReport.setIcon(summaryReportImage);
		miSummaryReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SummaryReport summaryReport = new SummaryReport(invoices);
				ReportFileManager.saveReportToTextFile(summaryReport, currentDate);
				SummaryReportParser summaryReportParser = new SummaryReportParser();
				Map<String, SummaryReport> summeryReports = summaryReportParser.parseDailyReport();
				summeryReports.forEach((date, report) -> {
					ReportFrame reportFrame = new ReportFrame(getConf(), summaryReport, date);
					reportFrame.setVisible(true);
				});
			}
		});

		miSpecialReport.setIcon(specialReportImage);
		miSpecialReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SpecialReport sr = new SpecialReport(invoices);
				ReportFileManager.saveReportToBinaryFile(sr);
				SpecialReport load = ReportFileManager.loadReportFromBinaryFile();
				SpecialReportFrame spf = new SpecialReportFrame(getConf(), load, getConf().getSpecialReportName());
				spf.setVisible(true);
			}
		});

		setJMenuBar(menuBar);

	}

	/**
	 * Creates the board with a grid layout and initializes its panels and labels.
	 */
	private void createBoard() {
		int rows = getConf().getTableYMax() + 1;
		int cols = getConf().getTableXMax() + 1;
		pnlBoard = new JPanel[rows][cols];
		lblVehicle = new JLabel[rows][cols];

		pnlMain = new JPanel(new GridLayout(rows, cols, 2, 2));
		initializeBoard(rows, cols);
		add(pnlMain, BorderLayout.CENTER);
		colorBoard();
	}

	/**
	 * Initializes the board by setting up each cell with a panel and a label.
	 *
	 * @param rows The number of rows in the board.
	 * @param cols The number of columns in the board.
	 */

	private void initializeBoard(int rows, int cols) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				pnlBoard[i][j] = new JPanel();
				lblVehicle[i][j] = new JLabel();

				setupBoardCell(pnlBoard[i][j], lblVehicle[i][j]);
				pnlMain.add(pnlBoard[i][j]);
			}
		}
	}

	/**
	 * Sets up a cell in the board with a label and default background color.
	 *
	 * @param panel The panel to be added to the cell.
	 * @param label The label to be added to the cell.
	 */
	private void setupBoardCell(JPanel panel, JLabel label) {
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder());
		panel.add(label);
		panel.setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * Colors the board based on the configuration settings.
	 */
	private void colorBoard() {
		int rows = getConf().getTableYMax() + 1;
		int cols = getConf().getTableXMax() + 1;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				pnlBoard[i][j].setBackground(Util.isNarrowArea(j, i) ? narrowColor : wideColor);
			}
		}
	}

	/**
	 * Initializes the vehicles array for the board.
	 */
	private void initializeVehicles() {
		int rows = getConf().getTableYMax() + 1;
		int cols = getConf().getTableXMax() + 1;
		vehicles = new ArrayList[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				vehicles[i][j] = new ArrayList<>();
			}
		}
	}

	public JLabel getLblDate() {
		return lblDate;
	}

	public JLabel getLblTime() {
		return lblTime;
	}

	public JLabel[][] getLblVehicle() {
		return lblVehicle;
	}

	public Color getCarColor() {
		return carColor;
	}

	public Color getBikeColor() {
		return bikeColor;
	}

	public Color getScooterColor() {
		return scooterColor;
	}

	public JPanel[][] getPnlBoard() {
		return pnlBoard;
	}

	public Color getWideColor() {
		return wideColor;
	}

	public Color getNarrowColor() {
		return narrowColor;
	}

	public List<TransportVehicle>[][] getVehicles() {
		return vehicles;
	}

}
