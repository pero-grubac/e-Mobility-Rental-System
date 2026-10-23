package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.ReportFileManager;
import net.etfbl.pj2.statistics.SpecialReport;
import net.etfbl.pj2.statistics.VehicleRevenue;

/**
 * Screen that deserializes the special report (the vehicle with the most
 * revenue of every type) from its binary files and shows every field of the
 * stored vehicles with their revenue.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class SpecialReportFrame extends GeneralFrame {
	private static final long serialVersionUID = 1L;

	private final List<Invoice> invoices;
	private final JTable table;
	private final JLabel empty;

	/**
	 * @param conf     The application configuration.
	 * @param invoices The invoices of all rentals; only generated ones are used
	 *                 when the report is serialized from this screen.
	 */
	public SpecialReportFrame(AppConfig conf, List<Invoice> invoices) {
		super(conf, conf.getText("SPECIAL_REPORT_TITLE"));
		this.invoices = invoices;
		setHint(String.format(conf.getText("SPECIAL_REPORT_HINT"), conf.getSpecialReportFolder()));

		table = createTable(conf.getText("COLUMN_TYPE"), conf.getText("COLUMN_ID"),
				conf.getText("COLUMN_MANUFACTURER"), conf.getText("COLUMN_MODEL"),
				conf.getText("COLUMN_PURCHASE_PRICE"), conf.getText("COLUMN_REVENUE"), conf.getText("COLUMN_RENTALS"),
				conf.getText("COLUMN_BATTERY_LEVEL"), conf.getText("COLUMN_MAX_SPEED"),
				conf.getText("COLUMN_RANGE_PER_CHARGE"), conf.getText("COLUMN_PURCHASE_DATE"),
				conf.getText("COLUMN_DESCRIPTION"), conf.getText("COLUMN_BREAKDOWNS"));

		empty = Theme.label(conf.getText("SPECIAL_REPORT_EMPTY"), Font.PLAIN, 13f, conf.getColor("MUTED_TEXT_COLOR"));
		JButton reload = new JButton(conf.getText("BUTTON_RELOAD"));
		reload.addActionListener(e -> load());
		JButton save = new JButton(conf.getText("BUTTON_SAVE_SPECIAL"));
		save.addActionListener(e -> serializeCurrentResults());

		JPanel bar = new JPanel(new BorderLayout());
		bar.setOpaque(false);
		bar.add(empty, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.setOpaque(false);
		buttons.add(reload);
		buttons.add(save);
		bar.add(buttons, BorderLayout.EAST);

		JPanel body = new JPanel(new BorderLayout(0, 10));
		body.setOpaque(false);
		body.add(tableCard(table, 3), BorderLayout.NORTH);
		body.add(bar, BorderLayout.CENTER);
		add(body, BorderLayout.CENTER);

		load();
		setSize(1500, 340);
	}

	/**
	 * Reads the serialized vehicles and shows them.
	 */
	private void load() {
		try {
			List<VehicleRevenue> revenues = ReportFileManager.loadSpecialReport(getConf());
			List<Object[]> rows = new ArrayList<>();
			revenues.forEach(r -> rows.add(row(r)));
			setRows(table, rows);
			empty.setVisible(rows.isEmpty());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), getTitle(), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Calculates the report from the invoices generated so far, serializes it
	 * and shows what was read back.
	 */
	private void serializeCurrentResults() {
		try {
			List<Invoice> generated = invoices.stream().filter(Invoice::isGenerated).toList();
			ReportFileManager.saveSpecialReport(new SpecialReport(generated), getConf());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), getTitle(), JOptionPane.ERROR_MESSAGE);
		}
		load();
	}

	private Object[] row(VehicleRevenue revenue) {
		AppConfig conf = getConf();
		TransportVehicle v = revenue.getVehicle();
		String maxSpeed = v instanceof ElectricScooter s ? String.valueOf(s.getMaxSpeed()) : "–";
		String range = v instanceof ElectricBike b ? String.valueOf(b.getRangePerCharge()) : "–";
		String date = "–";
		String description = "–";
		if (v instanceof Car car) {
			date = car.getPurchaseDate() == null ? "–" : car.getPurchaseDate().format(conf.getPurchaseDateFormatter());
			description = car.getDescription();
		}
		return new Object[] { conf.getVehicleTypeName(v.getType()), v.getId(), v.getManufacturer(), v.getModel(),
				BigDecimal.valueOf(v.getPurchasePrice()), revenue.getRevenue(), revenue.getRentals(),
				v.getBatteryText(), maxSpeed, range, date, description, v.getBreakdowns().size() };
	}
}
