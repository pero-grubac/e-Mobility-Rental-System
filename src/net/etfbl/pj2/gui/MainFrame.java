package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.Rental;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.simulation.SimulationListener;

/**
 * The main window: the live city map, the date and time of the running
 * rentals, the progress of the simulation, the active rentals and the latest
 * invoices, and buttons that open the other screens.
 * <p>
 * Simulation events arrive on simulation threads and are passed to the Swing
 * thread before they touch any component.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class MainFrame extends GeneralFrame implements SimulationListener {
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
	private static final int RECENT_INVOICES = 40;

	private final List<TransportVehicle> vehicles;
	private final List<Invoice> invoices;
	private final MapPanel map;
	private final JLabel lblDate = new JLabel("–");
	private final JLabel lblTime = new JLabel("–");
	private final JLabel lblStatus = new JLabel();
	private final JProgressBar progress;
	private final DefaultListModel<Invoice> activeRides = new DefaultListModel<>();
	private final DefaultListModel<Invoice> recentInvoices = new DefaultListModel<>();
	private final JList<Invoice> activeList = new JList<>(activeRides);
	private Timer pauseTimer;
	private int finished;

	private VehiclesFrame vehiclesFrame;
	private BreakdownsFrame breakdownsFrame;
	private ResultsFrame resultsFrame;
	private SpecialReportFrame specialReportFrame;

	/**
	 * Creates the main window.
	 *
	 * @param conf     The application configuration.
	 * @param vehicles All vehicles of the company.
	 * @param invoices The invoices of all rentals that will be simulated.
	 */
	public MainFrame(AppConfig conf, List<TransportVehicle> vehicles, List<Invoice> invoices) {
		super(conf, null);
		this.vehicles = vehicles;
		this.invoices = invoices;
		this.map = new MapPanel(conf);
		this.progress = new JProgressBar(0, Math.max(1, invoices.size()));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(createHeader(), BorderLayout.NORTH);
		RoundedPanel mapCard = new RoundedPanel(new BorderLayout(), 14);
		mapCard.add(map, BorderLayout.CENTER);
		add(mapCard, BorderLayout.CENTER);
		add(createSidebar(), BorderLayout.EAST);

		setStatus(conf.getText("STATUS_LOADING"), conf.getColor("MUTED_TEXT_COLOR"));
		updateProgress();
		setMinimumSize(new Dimension(1180, 760));
		setSize(1440, 920);
		setLocationRelativeTo(null);
	}

	// ---- Layout -------------------------------------------------------------

	private JPanel createHeader() {
		AppConfig conf = getConf();
		RoundedPanel header = new RoundedPanel(new BorderLayout(24, 0), 14);

		JPanel title = new JPanel();
		title.setOpaque(false);
		title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
		JLabel name = Theme.label(conf.getText("APP_TITLE"), Font.BOLD, 21f, conf.getColor("TEXT_COLOR"));
		name.setIcon(new ImageIcon(conf.getIconPath("APP_ICON")));
		name.setIconTextGap(10);
		title.add(name);
		JLabel subtitle = Theme.label(conf.getText("APP_SUBTITLE"), Font.PLAIN, 12.5f,
				conf.getColor("MUTED_TEXT_COLOR"));
		subtitle.setBorder(BorderFactory.createEmptyBorder(0, 42, 0, 0));
		title.add(subtitle);
		header.add(title, BorderLayout.WEST);

		JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 26, 4));
		stats.setOpaque(false);
		stats.add(stat(conf.getText("LABEL_DATE"), lblDate));
		stats.add(stat(conf.getText("LABEL_TIME"), lblTime));
		lblStatus.setFont(Theme.font(Font.BOLD, 14f));
		lblStatus.setIconTextGap(8);
		stats.add(stat(" ", lblStatus));

		progress.setStringPainted(true);
		progress.setFont(Theme.font(Font.PLAIN, 12f));
		progress.setPreferredSize(new Dimension(230, 22));
		JPanel progressBox = new JPanel(new BorderLayout(0, 4));
		progressBox.setOpaque(false);
		progressBox.add(Theme.label(" ", Font.PLAIN, 11.5f, conf.getColor("MUTED_TEXT_COLOR")), BorderLayout.NORTH);
		progressBox.add(progress, BorderLayout.CENTER);
		stats.add(progressBox);
		header.add(stats, BorderLayout.CENTER);
		return header;
	}

	private JPanel stat(String caption, JLabel value) {
		AppConfig conf = getConf();
		JPanel box = new JPanel();
		box.setOpaque(false);
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		box.add(Theme.label(caption.toUpperCase(), Font.BOLD, 11f, conf.getColor("MUTED_TEXT_COLOR")));
		if (value != lblStatus) {
			value.setFont(Theme.font(Font.BOLD, 19f));
			value.setForeground(conf.getColor("TEXT_COLOR"));
		}
		box.add(value);
		return box;
	}

	private JPanel createSidebar() {
		AppConfig conf = getConf();
		JPanel sidebar = new JPanel();
		sidebar.setOpaque(false);
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setPreferredSize(new Dimension(320, 100));
		sidebar.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

		RoundedPanel screens = new RoundedPanel(new GridLayout(0, 1, 0, 6), 12);
		screens.add(screenButton("VEHICLES_TITLE", "VEHICLES_ICON", () -> vehiclesFrame,
				() -> vehiclesFrame = new VehiclesFrame(conf, vehicles)));
		screens.add(screenButton("BREAKDOWNS_TITLE", "BREAKDOWNS_ICON", () -> breakdownsFrame,
				() -> breakdownsFrame = new BreakdownsFrame(conf, vehicles)));
		screens.add(screenButton("RESULTS_TITLE", "RESULTS_ICON", () -> resultsFrame,
				() -> resultsFrame = new ResultsFrame(conf, invoices)));
		screens.add(screenButton("SPECIAL_REPORT_TITLE", "SPECIAL_REPORT_ICON", () -> specialReportFrame,
				() -> specialReportFrame = new SpecialReportFrame(conf, invoices)));
		sidebar.add(fullWidth(screens, false));
		sidebar.add(Box.createVerticalStrut(12));
		sidebar.add(fullWidth(createLegend(), false));
		sidebar.add(Box.createVerticalStrut(12));

		activeList.setCellRenderer(new RideRenderer(true));
		activeList.setVisibleRowCount(5);
		sidebar.add(fullWidth(listCard(conf.getText("ACTIVE_RIDES_TITLE"), activeList, 230), false));
		sidebar.add(Box.createVerticalStrut(12));

		JList<Invoice> recentList = new JList<>(recentInvoices);
		recentList.setCellRenderer(new RideRenderer(false));
		sidebar.add(fullWidth(listCard(conf.getText("RECENT_INVOICES_TITLE"), recentList, Integer.MAX_VALUE), true));
		return sidebar;
	}

	/**
	 * Lets a sidebar card use the full width, at its preferred height unless it
	 * should grow into the remaining space.
	 */
	private static JPanel fullWidth(JPanel panel, boolean grow) {
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, grow ? Integer.MAX_VALUE : panel.getPreferredSize().height));
		return panel;
	}

	private JButton screenButton(String titleKey, String iconKey, Supplier<GeneralFrame> current,
			Supplier<GeneralFrame> create) {
		JButton button = new JButton(getConf().getText(titleKey), new ImageIcon(getConf().getIconPath(iconKey)));
		button.setFont(Theme.font(Font.PLAIN, 12.5f));
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setFocusable(false);
		button.addActionListener(e -> {
			GeneralFrame frame = current.get();
			if (frame == null || !frame.isDisplayable()) {
				frame = create.get();
				frame.setLocationRelativeTo(this);
			}
			frame.setVisible(true);
			frame.toFront();
		});
		return button;
	}

	private JPanel createLegend() {
		AppConfig conf = getConf();
		RoundedPanel legend = new RoundedPanel(new GridLayout(0, 3, 8, 8), 12);
		for (VehicleType type : VehicleType.values()) {
			legend.add(legendItem(conf.getVehicleTypeName(type), Theme.vehicleColor(conf, type), null));
		}
		legend.add(legendItem(conf.getText("NARROW_AREA_NAME"), conf.getColor("NARROW_AREA_COLOR"),
				conf.getColor("MAP_GRID_COLOR")));
		legend.add(legendItem(conf.getText("WIDE_AREA_NAME"), conf.getColor("WIDE_AREA_COLOR"),
				conf.getColor("MAP_GRID_COLOR")));
		return legend;
	}

	private JLabel legendItem(String text, Color color, Color border) {
		JLabel label = Theme.label(text, Font.PLAIN, 12.5f, getConf().getColor("TEXT_COLOR"));
		label.setIcon(new DotIcon(color, 14, border));
		label.setIconTextGap(8);
		return label;
	}

	private JPanel listCard(String title, JList<Invoice> list, int maxHeight) {
		RoundedPanel card = new RoundedPanel(new BorderLayout(0, 8), 12);
		card.add(Theme.label(title, Font.BOLD, 14f, getConf().getColor("TEXT_COLOR")), BorderLayout.NORTH);
		list.setOpaque(false);
		list.setFocusable(false);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		card.add(scroll, BorderLayout.CENTER);
		if (maxHeight != Integer.MAX_VALUE) {
			card.setPreferredSize(new Dimension(300, maxHeight));
		}
		return card;
	}

	/** Shows a rental with its vehicle color: active ones with battery, finished ones with their total. */
	private final class RideRenderer extends JPanel implements ListCellRenderer<Invoice> {
		private static final long serialVersionUID = 1L;
		private final boolean active;
		private final JLabel title = new JLabel();
		private final JLabel detail = new JLabel();
		private final JLabel value = new JLabel();

		RideRenderer(boolean active) {
			super(new BorderLayout(10, 0));
			this.active = active;
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 4));
			JPanel text = new JPanel(new GridLayout(2, 1));
			text.setOpaque(false);
			title.setFont(Theme.font(Font.BOLD, 13f));
			detail.setFont(Theme.font(Font.PLAIN, 11.5f));
			detail.setForeground(getConf().getColor("MUTED_TEXT_COLOR"));
			value.setFont(Theme.font(Font.BOLD, 13f));
			text.add(title);
			text.add(detail);
			add(text, BorderLayout.CENTER);
			add(value, BorderLayout.EAST);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Invoice> list, Invoice invoice, int index,
				boolean isSelected, boolean cellHasFocus) {
			AppConfig conf = getConf();
			Rental rental = invoice.getRental();
			TransportVehicle vehicle = invoice.getVehicle();
			title.setIcon(new DotIcon(Theme.vehicleColor(conf, vehicle.getType()), 11));
			title.setIconTextGap(8);
			title.setText(vehicle.getId() + "  ·  " + rental.getUser().getName());
			title.setForeground(conf.getColor("TEXT_COLOR"));
			if (active) {
				detail.setText("     " + rental.getStartLocation() + " → " + rental.getEndLocation() + "   "
						+ rental.getDurationInSeconds() + " s");
				value.setText(vehicle.getBatteryText());
				value.setForeground(conf.getColor("TEXT_COLOR"));
			} else {
				detail.setText("     " + rental.getEndTime().format(conf.getRentalDateTimeFormatter())
						+ (invoice.getBreakdown() != null ? "   ·   " + invoice.getBreakdown().getDescription() : ""));
				value.setText(Theme.money(invoice.getTotalAmount()));
				value.setForeground(invoice.getBreakdown() != null ? conf.getColor("NEGATIVE_COLOR")
						: conf.getColor("TEXT_COLOR"));
			}
			return this;
		}
	}

	// ---- Simulation events ----------------------------------------------------

	private void setStatus(String text, Color color) {
		lblStatus.setText(text);
		lblStatus.setForeground(color);
		lblStatus.setIcon(new DotIcon(color, 10));
	}

	private void updateProgress() {
		progress.setValue(finished);
		progress.setString(finished + " / " + invoices.size() + " " + getConf().getText("LABEL_FINISHED"));
	}

	private void stopPauseTimer() {
		if (pauseTimer != null) {
			pauseTimer.stop();
			pauseTimer = null;
		}
	}

	@Override
	public void timeSlotStarted(LocalDateTime startTime, int rentals) {
		SwingUtilities.invokeLater(() -> {
			stopPauseTimer();
			lblDate.setText(startTime.format(DATE));
			lblTime.setText(startTime.format(TIME));
			setStatus(getConf().getText("STATUS_RUNNING"), getConf().getColor("POSITIVE_COLOR"));
		});
	}

	@Override
	public void vehicleMoved(Invoice invoice, Field field) {
		SwingUtilities.invokeLater(() -> {
			if (!activeRides.contains(invoice)) {
				activeRides.addElement(invoice);
			}
			map.moveVehicle(invoice, field);
			activeList.repaint();
		});
	}

	@Override
	public void rentalFinished(Invoice invoice) {
		SwingUtilities.invokeLater(() -> {
			map.removeVehicle(invoice);
			activeRides.removeElement(invoice);
			recentInvoices.add(0, invoice);
			if (recentInvoices.size() > RECENT_INVOICES) {
				recentInvoices.remove(recentInvoices.size() - 1);
			}
			finished++;
			updateProgress();
		});
	}

	@Override
	public void paused(int seconds) {
		SwingUtilities.invokeLater(() -> {
			stopPauseTimer();
			int[] left = { seconds };
			Color amber = new Color(0xD97706);
			setStatus(String.format(getConf().getText("STATUS_PAUSED"), left[0]), amber);
			pauseTimer = new Timer(1000, e -> {
				left[0] = Math.max(0, left[0] - 1);
				setStatus(String.format(getConf().getText("STATUS_PAUSED"), left[0]), amber);
			});
			pauseTimer.start();
		});
	}

	@Override
	public void finished() {
		SwingUtilities.invokeLater(() -> {
			stopPauseTimer();
			setStatus(getConf().getText("STATUS_FINISHED"), getConf().getColor("ACCENT_COLOR"));
			JOptionPane.showMessageDialog(this, getConf().getText("END_MESSAGE"), getConf().getText("APP_TITLE"),
					JOptionPane.INFORMATION_MESSAGE);
		});
	}
}
