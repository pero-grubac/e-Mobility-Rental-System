package net.etfbl.pj2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.SummaryReport;
import net.etfbl.pj2.util.Util;

/**
 * Screen with the business results of all invoices generated so far: the
 * summary report as cards and the daily report as one table with a row per
 * date.
 *
 * @author Pero Grubač
 * @since 23.10.2026.
 */
public class ResultsFrame extends GeneralFrame {
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private final List<Invoice> invoices;
	private final Map<String, JLabel> summaryValues = new LinkedHashMap<>();
	private final Map<String, Function<SummaryReport, BigDecimal>> summaryItems = new LinkedHashMap<>();
	private final JTable dailyTable;

	/**
	 * @param conf     The application configuration.
	 * @param invoices The invoices of all rentals; only generated ones are used.
	 */
	public ResultsFrame(AppConfig conf, List<Invoice> invoices) {
		super(conf, conf.getText("RESULTS_TITLE"));
		this.invoices = invoices;

		summaryItems.put("COLUMN_TOTAL_INCOME", SummaryReport::getTotalIncome);
		summaryItems.put("COLUMN_TOTAL_DISCOUNT", SummaryReport::getTotalDiscount);
		summaryItems.put("COLUMN_TOTAL_PROMOTION", SummaryReport::getTotalPromotions);
		summaryItems.put("COLUMN_NARROW_AREA_INCOME", SummaryReport::getTotalIncomeInNarrowArea);
		summaryItems.put("COLUMN_WIDE_AREA_INCOME", SummaryReport::getTotalIncomeInWideArea);
		summaryItems.put("COLUMN_MAINTENANCE", SummaryReport::getTotalAmountForMaintenance);
		summaryItems.put("COLUMN_REPAIRS", SummaryReport::getTotalAmountForRepairs);
		summaryItems.put("COLUMN_TOTAL_COST", SummaryReport::getTotalCost);
		summaryItems.put("COLUMN_TOTAL_TAX", SummaryReport::getTotalTax);

		JPanel cards = new JPanel(new GridLayout(3, 3, 10, 10));
		cards.setOpaque(false);
		summaryItems.keySet().forEach(key -> cards.add(card(key)));

		dailyTable = createTable(conf.getText("COLUMN_DATE"), conf.getText("COLUMN_RENTALS"),
				conf.getText("COLUMN_TOTAL_INCOME"), conf.getText("COLUMN_TOTAL_DISCOUNT"),
				conf.getText("COLUMN_TOTAL_PROMOTION"), conf.getText("COLUMN_NARROW_AREA_INCOME"),
				conf.getText("COLUMN_WIDE_AREA_INCOME"), conf.getText("COLUMN_MAINTENANCE"),
				conf.getText("COLUMN_REPAIRS"));
		refreshEverySecond(this::refresh);

		JPanel body = new JPanel(new BorderLayout(0, 4));
		body.setOpaque(false);
		JPanel summary = new JPanel(new BorderLayout());
		summary.setOpaque(false);
		summary.add(sectionTitle(conf.getText("SUMMARY_TITLE"), conf.getColor("ACCENT_COLOR")), BorderLayout.NORTH);
		summary.add(cards, BorderLayout.CENTER);
		body.add(summary, BorderLayout.NORTH);
		JPanel daily = new JPanel(new BorderLayout());
		daily.setOpaque(false);
		daily.add(sectionTitle(conf.getText("DAILY_TITLE"), conf.getColor("ACCENT_COLOR")), BorderLayout.NORTH);
		daily.add(tableCard(dailyTable, 4), BorderLayout.CENTER);
		body.add(daily, BorderLayout.CENTER);
		add(body, BorderLayout.CENTER);

		setSize(1180, 700);
	}

	private JPanel card(String key) {
		AppConfig conf = getConf();
		RoundedPanel card = new RoundedPanel(new BorderLayout(0, 4), 14);
		card.add(Theme.label(conf.getText(key).toUpperCase(), Font.BOLD, 11f, conf.getColor("MUTED_TEXT_COLOR")),
				BorderLayout.NORTH);
		JLabel value = Theme.label("0.00", Font.BOLD, 22f, conf.getColor("TEXT_COLOR"));
		value.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		card.add(value, BorderLayout.CENTER);
		summaryValues.put(key, value);
		return card;
	}

	private void refresh() {
		AppConfig conf = getConf();
		List<Invoice> generated = invoices.stream().filter(Invoice::isGenerated).toList();
		setHint(conf.getText("RESULTS_HINT") + ": " + generated.size() + " / " + invoices.size());

		SummaryReport summary = new SummaryReport(generated, conf);
		summaryItems.forEach((key, getter) -> {
			BigDecimal value = getter.apply(summary);
			JLabel label = summaryValues.get(key);
			label.setText(Theme.money(value));
			Color color = value.signum() < 0 ? conf.getColor("NEGATIVE_COLOR") : conf.getColor("TEXT_COLOR");
			label.setForeground(color);
		});

		List<Object[]> rows = new ArrayList<>();
		for (Map.Entry<LocalDate, List<Invoice>> day : Util.groupByDate(generated).entrySet()) {
			DailyReport report = new DailyReport(day.getKey(), day.getValue(), conf);
			rows.add(new Object[] { day.getKey().format(DATE),
					report.getRentals(), report.getTotalIncome(), report.getTotalDiscount(),
					report.getTotalPromotions(), report.getTotalIncomeInNarrowArea(), report.getTotalIncomeInWideArea(),
					report.getTotalAmountForMaintenance(), report.getTotalAmountForRepairs() });
		}
		setRows(dailyTable, rows);
	}
}
