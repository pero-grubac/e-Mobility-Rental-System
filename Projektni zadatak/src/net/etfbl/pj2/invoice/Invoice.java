package net.etfbl.pj2.invoice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Invoice {
	private String rentailId;
	private String userId;
	private String userName;
	private BigDecimal totalAmount;
	private Double discount;
	private Double promotion;
	private Double wideArea;
	private Double breakdownDeduction;
	private List<String> items;
	
	public void generateInvoice() {
        StringBuilder invoiceContent = new StringBuilder();
        invoiceContent.append("Invoice for Rental ID: ").append(rentailId).append("\n");
        invoiceContent.append("User ID: ").append(userId).append("\n");
        invoiceContent.append("User Name: ").append(userName).append("\n");
        invoiceContent.append("Total Amount: ").append(totalAmount).append("\n");
        invoiceContent.append("Discount Amount: ").append(discount).append("\n");
        invoiceContent.append("Promotion Amount: ").append(promotion).append("\n");
        invoiceContent.append("Wide Area Amount: ").append(wideArea).append("\n");
        invoiceContent.append("Breakdown Deduction: ").append(breakdownDeduction).append("\n");
        invoiceContent.append("Detailed Items: ").append(items).append("\n");
        System.out.println(invoiceContent);
        // Sacuvaj kao fajl
        SimpleDateFormat  sdf =new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateAndTime = sdf.format(new Date());
        String fileName = userName + " - " + currentDateAndTime + ".txt";
        
        String filePath = System.getProperty("user.dir") + File.separator + "invoice" + File.separator + fileName;
        File invoiceFolder = new File(System.getProperty("user.dir") + File.separator + "invoice");
        if (!invoiceFolder.exists()) {
            invoiceFolder.mkdir();
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(invoiceContent.toString());
            System.out.println("Invoice saved successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving invoice: " + e.getMessage());
        }
	}
}
