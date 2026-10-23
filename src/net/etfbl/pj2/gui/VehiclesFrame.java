package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.VehicleType;
import net.etfbl.pj2.resources.AppConfig;

/**
 * Screen with all vehicles of the company: one table for cars, one for bikes
 * and one for scooters, with every field. Battery levels and breakdowns are
 * refreshed while the simulation runs.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class VehiclesFrame extends GeneralFrame {
	private static final long serialVersionUID = 1L;

	private final List<TransportVehicle> vehicles;
	private final Map<VehicleType, JTable> tables = new EnumMap<>(VehicleType.class);

	/**
	 * @param conf     The application configuration.
	 * @param vehicles All vehicles.
	 */
	public VehiclesFrame(AppConfig conf, List<TransportVehicle> vehicles) {
		super(conf, conf.getText("VEHICLES_TITLE"));
		this.vehicles = vehicles;
		setHint(vehicles.size() + " vehicles");

		tables.put(VehicleType.CAR, createTable(conf.getText("COLUMN_ID"), conf.getText("COLUMN_MANUFACTURER"),
				conf.getText("COLUMN_MODEL"), conf.getText("COLUMN_PURCHASE_DATE"),
				conf.getText("COLUMN_PURCHASE_PRICE"), conf.getText("COLUMN_DESCRIPTION"),
				conf.getText("COLUMN_MULTIPLE_PEOPLE"), conf.getText("COLUMN_BATTERY_LEVEL"),
				conf.getText("COLUMN_BREAKDOWNS")));
		tables.put(VehicleType.BIKE, createTable(conf.getText("COLUMN_ID"), conf.getText("COLUMN_MANUFACTURER"),
				conf.getText("COLUMN_MODEL"), conf.getText("COLUMN_PURCHASE_PRICE"),
				conf.getText("COLUMN_RANGE_PER_CHARGE"), conf.getText("COLUMN_BATTERY_LEVEL"),
				conf.getText("COLUMN_BREAKDOWNS")));
		tables.put(VehicleType.SCOOTER, createTable(conf.getText("COLUMN_ID"), conf.getText("COLUMN_MANUFACTURER"),
				conf.getText("COLUMN_MODEL"), conf.getText("COLUMN_PURCHASE_PRICE"),
				conf.getText("COLUMN_MAX_SPEED"), conf.getText("COLUMN_BATTERY_LEVEL"),
				conf.getText("COLUMN_BREAKDOWNS")));
		refreshEverySecond(this::refresh);

		JPanel sections = new JPanel();
		sections.setOpaque(false);
		sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
		for (VehicleType type : VehicleType.values()) {
			long count = vehicles.stream().filter(v -> v.getType() == type).count();
			JLabel title = sectionTitle(conf.getVehicleTypeName(type) + "  (" + count + ")",
					Theme.vehicleColor(conf, type));
			title.setAlignmentX(Component.LEFT_ALIGNMENT);
			sections.add(title);
			JPanel card = tableCard(tables.get(type), (int) count);
			card.setAlignmentX(Component.LEFT_ALIGNMENT);
			sections.add(card);
		}
		JScrollPane scroll = new JScrollPane(sections);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll, BorderLayout.CENTER);

		setSize(1100, 820);
	}

	private void refresh() {
		AppConfig conf = getConf();
		Map<VehicleType, List<Object[]>> rows = new EnumMap<>(VehicleType.class);
		for (VehicleType type : VehicleType.values())
			rows.put(type, new ArrayList<>());

		for (TransportVehicle v : vehicles) {
			BigDecimal price = BigDecimal.valueOf(v.getPurchasePrice());
			int breakdowns = v.getBreakdowns().size();
			if (v instanceof Car car) {
				rows.get(VehicleType.CAR).add(new Object[] { car.getId(), car.getManufacturer(), car.getModel(),
						car.getPurchaseDate() == null ? "–" : car.getPurchaseDate().format(conf.getPurchaseDateFormatter()),
						price, car.getDescription(), car.canCarryMultiplePeople() ? "Yes" : "No", car.getBatteryText(),
						breakdowns });
			} else if (v instanceof ElectricBike bike) {
				rows.get(VehicleType.BIKE).add(new Object[] { bike.getId(), bike.getManufacturer(), bike.getModel(),
						price, bike.getRangePerCharge(), bike.getBatteryText(), breakdowns });
			} else if (v instanceof ElectricScooter scooter) {
				rows.get(VehicleType.SCOOTER).add(new Object[] { scooter.getId(), scooter.getManufacturer(),
						scooter.getModel(), price, scooter.getMaxSpeed(), scooter.getBatteryText(), breakdowns });
			}
		}
		rows.forEach((type, list) -> setRows(tables.get(type), list));
	}
}
