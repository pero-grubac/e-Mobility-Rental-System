package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

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

	private Color carColor = Color.RED;
	private Color bikeColor = Color.BLUE;
	private Color scooterColor = Color.GREEN;
	private Color wideColor = Color.decode("#FFCD80");
	private Color narrowColor = Color.decode("#B3FF80");

	public MainFrame(AppConfig conf) {
		setTitle("eMobility");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		String frameIconPath = conf.getIconsFolder() + File.separator + conf.getFrameIcon();
		ImageIcon frameIcon = new ImageIcon(frameIconPath);
		setIconImage(frameIcon.getImage());

		pnlDate = new JPanel();
		lblDate = new JLabel("Date: ");
		pnlDate.add(lblDate);
		pnlDate.setBackground(Color.decode("#80B3FF"));

		pnlTime = new JPanel();
		lblTime = new JLabel("Time: ");
		pnlTime.add(lblTime);
		pnlTime.setBackground(Color.decode("#80B3FF"));

		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(pnlDate, BorderLayout.NORTH);
		pnlNorth.add(pnlTime, BorderLayout.SOUTH);
		pnlNorth.setSize(getWidth(), 3);

		setLayout(new BorderLayout());
		add(pnlNorth, BorderLayout.NORTH);

		createMenuBar(conf);

		createBoard(conf);

		// pack();
		// setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
	}

	private void createMenuBar(AppConfig conf) {

		menuBar = new JMenuBar();
		menuReport = new JMenu("Report");
		menuBar.add(menuReport);

		miDailyReport = new JMenuItem("Daily Report");
		miSummaryReport = new JMenuItem("Summary Report");
		miSpecialReport = new JMenuItem("Special Report");
		menuReport.add(miDailyReport);
		menuReport.add(miSummaryReport);
		menuReport.add(miSpecialReport);

		String dailyReportImagePath = conf.getIconsFolder() + File.separator + conf.getDailyReportIcon();
		String summaryReportImagePath = conf.getIconsFolder() + File.separator + conf.getSummaryReportIcon();
		String specialReportImagePath = conf.getIconsFolder() + File.separator + conf.getSpecialReportIcon();

		ImageIcon dailyReportImage = new ImageIcon(dailyReportImagePath);
		ImageIcon summaryReportImage = new ImageIcon(summaryReportImagePath);
		ImageIcon specialReportImage = new ImageIcon(specialReportImagePath);

		miDailyReport.setIcon(dailyReportImage);
		miDailyReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("daily report");

			}
		});

		miSummaryReport.setIcon(summaryReportImage);
		miSummaryReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("summary");
			}
		});

		miSpecialReport.setIcon(specialReportImage);
		miSpecialReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("special");
			}
		});

		this.setJMenuBar(menuBar);
	}

	private void createBoard(AppConfig conf) {
		int x = conf.getTableXMax() + 1;
		int y = conf.getTableYMax() + 1;
		pnlBoard = new JPanel[y][x];
		lblVehicle = new JLabel[y][x];

		pnlMain = new JPanel(new GridLayout(y, x, 2, 2));

		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				pnlBoard[i][j] = new JPanel();
				lblVehicle[i][j] = new JLabel();

				lblVehicle[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				lblVehicle[i][j].setVerticalAlignment(SwingConstants.CENTER);
				lblVehicle[i][j].setBorder(BorderFactory.createEmptyBorder());
				pnlBoard[i][j].add(lblVehicle[i][j]);

				pnlBoard[i][j].setBackground(Color.LIGHT_GRAY);

				pnlMain.add(pnlBoard[i][j]);
			}
		}

		add(pnlMain, BorderLayout.CENTER);
		colorBoard(conf);
		/*
		 * int panelWidth = pnlBoard[0][0].getPreferredSize().width; int panelHeight =
		 * pnlBoard[0][0].getPreferredSize().height; int frameWidth = panelWidth * x;
		 * int frameHeight = panelHeight * y + pnlNorth.getPreferredSize().height +
		 * menuBar.getPreferredSize().height; setPreferredSize(new Dimension(frameWidth,
		 * frameHeight)); pack();
		 */
		// JScrollPane scrollPane = new JScrollPane(pnlMain);
		// scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// add(scrollPane, BorderLayout.CENTER);
	}

	private void colorBoard(AppConfig conf) {
		int x = conf.getTableXMax() + 1;
		int y = conf.getTableYMax() + 1;
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				if(Util.isNarrowArea(j, i))
					pnlBoard[i][j].setBackground(narrowColor);
				else 
					pnlBoard[i][j].setBackground(wideColor);
				
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

	

	
	
}
