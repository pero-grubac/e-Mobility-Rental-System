package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;

import net.etfbl.pj2.breakdown.Breakdown;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Screen with every breakdown so far: vehicle type, vehicle ID, time and
 * description, oldest first.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class BreakdownsFrame extends GeneralFrame {
	private static final long serialVersionUID = 1L;

	private final List<TransportVehicle> vehicles;
	private final JTable table;

	/**
	 * @param conf     The application configuration.
	 * @param vehicles All vehicles.
	 */
	public BreakdownsFrame(AppConfig conf, List<TransportVehicle> vehicles) {
		super(conf, conf.getText("BREAKDOWNS_TITLE"));
		this.vehicles = vehicles;
		table = createTable(conf.getText("COLUMN_TYPE"), conf.getText("COLUMN_ID"), conf.getText("COLUMN_TIME"),
				conf.getText("COLUMN_DESCRIPTION"));
		refreshEverySecond(this::refresh);
		add(tableCard(table, 10), BorderLayout.CENTER);
		setSize(760, 520);
	}

	/** A breakdown together with the vehicle it belongs to. */
	private record Entry(TransportVehicle vehicle, Breakdown breakdown) {
	}

	private void refresh() {
		AppConfig conf = getConf();
		List<Object[]> rows = vehicles.stream()
				.flatMap(v -> v.getBreakdowns().stream().map(b -> new Entry(v, b)))
				.sorted(Comparator.comparing(e -> e.breakdown().getDateTime()))
				.map(e -> new Object[] { conf.getVehicleTypeName(e.vehicle().getType()), e.vehicle().getId(),
						e.breakdown().getDateTime().format(Breakdown.DATE_TIME_FORMATTER),
						e.breakdown().getDescription() })
				.toList();
		setRows(table, rows);
		setHint(rows.size() + (rows.size() == 1 ? " breakdown" : " breakdowns"));
	}
}
