package net.etfbl.pj2.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;

public class DailyReport implements Serializable {
	private static final long serialVersionUID = 1L;

	private transient Statistics statistcs;
	private BigDecimal totalIncome;
	private BigDecimal totalDiscount;
	private BigDecimal totalPromotions;
	private BigDecimal totalIncomeInWideArea;
	private BigDecimal totalIncomeInNarrowArea;
	private BigDecimal totalAmountForMaintence;
	private BigDecimal totalAmountForRepairs;
	private List<TransportVehicle> vehicles = new ArrayList<TransportVehicle>(new HashSet<TransportVehicle>());

	public DailyReport(List<Invoice> invoices) {
		statistcs = new Statistics(invoices);
		invoices.stream().forEach(invoice -> vehicles.add(invoice.getVehicle()));
		totalIncome = statistcs.calculateTotalIncome();
		totalDiscount = statistcs.calculateTotalDiscount();
		totalPromotions = statistcs.calculateTotalPromotions();
		totalIncomeInNarrowArea = statistcs.calculateTotalIncomeInNarrowArea();
		totalIncomeInWideArea = statistcs.calculateTotalIncomeInWideArea();
		totalAmountForMaintence = statistcs.calculateTotalAmountForMaintence(totalIncome);
		totalAmountForRepairs = statistcs.calculateTotalAmountForRepairs();
	}

	private String generateReportText() {
		StringBuilder reportContent = new StringBuilder();
		reportContent.append("Total Income: ").append(totalIncome).append("\n");
		reportContent.append("Total Discount: ").append(totalDiscount).append("\n");
		reportContent.append("Total Promotions: ").append(totalPromotions).append("\n");
		reportContent.append("Total Income in Wide Area: ").append(totalIncomeInWideArea).append("\n");
		reportContent.append("Total Income in Narrow Area: ").append(totalIncomeInNarrowArea).append("\n");
		reportContent.append("Total Amount for Maintenance: ").append(totalAmountForMaintence).append("\n");
		reportContent.append("Total Amount for Repairs: ").append(totalAmountForRepairs).append("\n");
		reportContent.append("List of Unique Vehicles: ").append("\n");

		for (TransportVehicle vehicle : vehicles) {
			reportContent.append("   ").append(vehicle).append("\n");
		}

		return reportContent.toString();
	}

	@Override
	public String toString() {
		return generateReportText();
	}

}
