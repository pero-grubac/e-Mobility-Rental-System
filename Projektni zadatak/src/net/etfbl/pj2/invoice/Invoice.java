package net.etfbl.pj2.invoice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.etfbl.pj2.breakdown.Breakdown;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.User;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;

public class Invoice {

	private BigDecimal totalAmount;
	private Double basicPrice = 0.0;
	private Double distance = 0.0;
	private Double distanceNarrow = 0.0;
	private Double distanceWide = 0.0;
	// u Rental ima isBreakdown
	private Double discount = 0.0;
	private Double promotion = 0.0;
	private Rental rental;
	private TransportVehicle vehicle;
	private transient Breakdown breakdown;
	private transient Integer lengthNarrow = 0;
	private transient Integer lengthWide = 0;

	public Invoice(Rental rental, TransportVehicle vehicle) {
		this.rental = rental;
		this.vehicle = vehicle;
		AppConfig conf = new AppConfig();
		calculateBasicPrice(conf);
		calculateDistance(conf);
		calculateDiscount(conf);
		calculatePromotion(conf);
		calculateTotal();
	}

	private void calculateBasicPrice(AppConfig conf) {
		if (this.rental.getIsBreakdown())
			basicPrice = 0.0;
		else {

			if (vehicle instanceof Car)
				basicPrice = conf.getCarUnitPrice();
			else if (vehicle instanceof ElectricScooter)
				basicPrice = conf.getScooterUnitPrice();
			else if (vehicle instanceof ElectricBike)
				basicPrice = conf.getBikeUnitPrice();
			else
				System.out.println("greska");
			// baci izuzetak
		}
	}

	private void calculateDistance(AppConfig conf) {
		distanceNarrow = conf.getDistanceNarrow();
		distanceWide = conf.getDistanceWide();
		for (Field field : rental.getShortestPath()) {
			if (field.getX() <= 10 || field.getY() <= 10) {
				distance += basicPrice * distanceWide;
				++lengthWide;
			} else {
				distance += basicPrice * distanceNarrow;
				++lengthNarrow;
			}
		}
	}

	private void calculateDiscount(AppConfig conf) {
		if (rental.getIsDiscount())
			discount = basicPrice * conf.getDiscount();
	}

	private void calculatePromotion(AppConfig conf) {
		if (rental.getIsPromotion())
			promotion = basicPrice * conf.getDiscountProm();
	}

	private void calculateTotal() {
		AppConfig conf = new AppConfig();
		totalAmount = BigDecimal.valueOf(distance + discount + promotion)
				.setScale(conf.getBigDecimalRound(), RoundingMode.HALF_UP);
	}

	public void generateInvoice(AppConfig conf) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		String currentDateAndTime = sdf.format(new Date());
		Random rand = new Random();
		// prebaci na endtime
		String fileName = rental.getUser().getName() + " - " + currentDateAndTime + rand.nextInt()
				+ conf.getInvoiceDocType();

		String filePath = conf.getInvoiceFolder() + File.separator + fileName;
		File invoiceFolder = new File(conf.getInvoiceFolder());
		if (!invoiceFolder.exists()) {
			invoiceFolder.mkdir();
		}

		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write(generateInvoiceText());
			System.out.println("Invoice saved successfully: " + filePath);
		} catch (IOException e) {
			System.err.println("Error saving invoice: " + e.getMessage());
		}

	}

	private String generateInvoiceText() {
		StringBuilder invoiceContent = new StringBuilder();
		invoiceContent.append(rental.getUser()).append("\n");
		invoiceContent.append("Vehicle ID: ").append(vehicle.getId()).append("\n");
		invoiceContent.append("Basic Price: ").append(String.format("%.2f", basicPrice)).append("\n");
		invoiceContent.append("Start: ").append(rental.getStartLocation()).append("\n");
		invoiceContent.append("End: ").append(rental.getEndLocation()).append("\n");
		invoiceContent.append("Length: ").append(rental.getShortestPath().size()).append("\n");
		invoiceContent.append("Distance Narrow Area: ").append(String.format("%.2f", distanceNarrow)).append(" (x")
				.append(lengthNarrow).append(")").append("\n");
		invoiceContent.append("Distance Wide Area: ").append(String.format("%.2f", distanceWide)).append(" (x")
				.append(lengthWide).append(")").append("\n");
		invoiceContent.append("Distance: ").append(String.format("%.2f", distance)).append("\n");
		invoiceContent.append("Discount: ").append(String.format("%.2f", discount)).append("\n");
		invoiceContent.append("Promotion: ").append(String.format("%.2f", promotion)).append("\n");
		invoiceContent.append("Total Amount: ").append(totalAmount).append("\n");
// dodaj starttime i endtime
		if (rental.getIsBreakdown()) {
			breakdown = new Breakdown(rental.getStartTime());
			invoiceContent.append(breakdown).append("\n");

		} else {
			invoiceContent.append("Breakdown: No").append("\n");
		}
		return invoiceContent.toString();
	}

	public TransportVehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(TransportVehicle vehicle) {
		this.vehicle = vehicle;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	

	public Double getPromotion() {
		return promotion;
	}

	public void setPromotion(Double promotion) {
		this.promotion = promotion;
	}

	public Double getBasicPrice() {
		return basicPrice;
	}

	public void setBasicPrice(Double basicPrice) {
		this.basicPrice = basicPrice;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getDistanceNarrow() {
		return distanceNarrow;
	}

	public void setDistanceNarrow(Double distanceNarrow) {
		this.distanceNarrow = distanceNarrow;
	}

	public Double getDistanceWide() {
		return distanceWide;
	}

	public void setDistanceWide(Double distanceWide) {
		this.distanceWide = distanceWide;
	}

	public Rental getRental() {
		return rental;
	}

	public void setRental(Rental rental) {
		this.rental = rental;
	}

	public Breakdown getBreakdown() {
		return breakdown;
	}

	public void setBreakdown(Breakdown breakdown) {
		this.breakdown = breakdown;
	}

	public Integer getLengthNarrow() {
		return lengthNarrow;
	}

	public void setLengthNarrow(Integer lengthNarrow) {
		this.lengthNarrow = lengthNarrow;
	}

	public Integer getLengthWide() {
		return lengthWide;
	}

	public void setLengthWide(Integer lengthWide) {
		this.lengthWide = lengthWide;
	}

	@Override
	public String toString() {
		return generateInvoiceText();
	}

}
