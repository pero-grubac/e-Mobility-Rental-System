package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.spi.TransactionalWriter;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.simulation.Simulation;
import net.etfbl.pj2.util.Util;

public class MainFrame extends JFrame {

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

	private Color carColor = Color.RED;
	private Color bikeColor = Color.BLUE;
	private Color scooterColor = Color.GREEN;
	private Color wideColor = Color.decode("#FFCD80");
	private Color narrowColor = Color.decode("#B3FF80");

	public MainFrame(AppConfig conf) {
		configureFrame(conf);
		createMenuBar(conf);
		createBoard(conf);
		initializeVehicles(conf);
	}

	private void configureFrame(AppConfig conf) {
		setTitle("eMobility");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon(conf.getIconsFolder() + File.separator + conf.getFrameIcon()).getImage());

		pnlDate = createLabelPanel("Date: ", lblDate = new JLabel());
		pnlTime = createLabelPanel("Time: ", lblTime = new JLabel());

		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(pnlDate, BorderLayout.NORTH);
		pnlNorth.add(pnlTime, BorderLayout.SOUTH);
		pnlNorth.setSize(getWidth(), 3);

		setLayout(new BorderLayout());
		add(pnlNorth, BorderLayout.NORTH);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private JPanel createLabelPanel(String labelText, JLabel label) {
		JPanel panel = new JPanel();
		label.setText(labelText);
		panel.add(label);
		panel.setBackground(Color.decode("#80B3FF"));
		return panel;
	}

	private void createMenuBar(AppConfig conf) {
		menuBar = new JMenuBar();
		menuReport = new JMenu("Report");
		menuBar.add(menuReport);

		miDailyReport = createMenuItem("Daily Report",
				conf.getIconsFolder() + File.separator + conf.getDailyReportIcon(),
				e -> System.out.println("daily report"));
		miSummaryReport = createMenuItem("Summary Report",
				conf.getIconsFolder() + File.separator + conf.getSummaryReportIcon(),
				e -> System.out.println("summary"));
		miSpecialReport = createMenuItem("Special Report",
				conf.getIconsFolder() + File.separator + conf.getSpecialReportIcon(),
				e -> System.out.println("special"));

		menuReport.add(miDailyReport);
		menuReport.add(miSummaryReport);
		menuReport.add(miSpecialReport);

		setJMenuBar(menuBar);
	}

	private JMenuItem createMenuItem(String text, String iconPath, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(text, new ImageIcon(iconPath));
		menuItem.addActionListener(actionListener);
		return menuItem;
	}

	private void createBoard(AppConfig conf) {
		int rows = conf.getTableYMax() + 1;
		int cols = conf.getTableXMax() + 1;
		pnlBoard = new JPanel[rows][cols];
		lblVehicle = new JLabel[rows][cols];

		pnlMain = new JPanel(new GridLayout(rows, cols, 2, 2));
		initializeBoard(rows, cols);
		add(pnlMain, BorderLayout.CENTER);
		colorBoard(conf);
	}

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

	private void setupBoardCell(JPanel panel, JLabel label) {
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder());
		panel.add(label);
		panel.setBackground(Color.LIGHT_GRAY);
	}

	private void colorBoard(AppConfig conf) {
		int rows = conf.getTableYMax() + 1;
		int cols = conf.getTableXMax() + 1;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				pnlBoard[i][j].setBackground(Util.isNarrowArea(j, i) ? narrowColor : wideColor);
			}
		}
	}

	private void initializeVehicles(AppConfig conf) {
		int rows = conf.getTableYMax() + 1;
		int cols = conf.getTableXMax() + 1;
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
