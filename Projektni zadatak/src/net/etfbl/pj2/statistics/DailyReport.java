package net.etfbl.pj2.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.TransportVehicle;

public class DailyReport {

	protected transient Statistics statistcs;
	protected BigDecimal totalIncome;
	protected BigDecimal totalDiscount;
	protected BigDecimal totalPromotions;
	protected BigDecimal totalIncomeInWideArea;
	protected BigDecimal totalIncomeInNarrowArea;
	protected BigDecimal totalAmountForMaintence;
	protected BigDecimal totalAmountForRepairs;
	protected List<TransportVehicle> vehicles = new ArrayList<TransportVehicle>(new HashSet<TransportVehicle>());
	protected LocalDate date;

	public DailyReport() {
	}

	public DailyReport(List<Invoice> invoices) {
		statistcs = new Statistics(invoices);
		vehicles = invoices.stream()
                .map(Invoice::getVehicle) 
                .distinct() 
                .collect(Collectors.toList());
		totalIncome = statistcs.calculateTotalIncome();
		totalDiscount = statistcs.calculateTotalDiscount();
		totalPromotions = statistcs.calculateTotalPromotions();
		totalIncomeInNarrowArea = statistcs.calculateTotalIncomeInNarrowArea();
		totalIncomeInWideArea = statistcs.calculateTotalIncomeInWideArea();
		totalAmountForMaintence = statistcs.calculateTotalAmountForMaintence(totalIncome);
		totalAmountForRepairs = statistcs.calculateTotalAmountForRepairs();
	}

	protected String generateReportText() {
		StringBuilder reportContent = new StringBuilder();
		reportContent.append("Total Income: ").append(totalIncome).append("\n");
		reportContent.append("Total Discount: ").append(totalDiscount).append("\n");
		reportContent.append("Total Promotions: ").append(totalPromotions).append("\n");
		reportContent.append("Total Income in Wide Area: ").append(totalIncomeInWideArea).append("\n");
		reportContent.append("Total Income in Narrow Area: ").append(totalIncomeInNarrowArea).append("\n");
		reportContent.append("Total Amount for Maintenance: ").append(totalAmountForMaintence).append("\n");
		reportContent.append("Total Amount for Repairs: ").append(totalAmountForRepairs).append("\n");

		return reportContent.toString();
	}

	protected String reportWithVehicles() {
		StringBuilder reportContent = new StringBuilder();
		reportContent.append(generateReportText());
		reportContent.append("List of Unique Vehicles: ").append("\n");

		for (TransportVehicle vehicle : vehicles) {
			reportContent.append("   ").append(vehicle).append("\n");
		}
		return reportContent.toString();
	}

	@Override
	public String toString() {
		return reportWithVehicles();
	}

}
