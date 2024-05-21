package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.rental.Rental;

public class RentalParser {

	public List<Rental> parseRentals(String filePath) {
		List<Rental> rentals = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
			String line;
			// preskociti zaglavlje
			br.readLine();	
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				
				
				LocalDateTime startDate = LocalDateTime.parse(values[0].replace("\"", ""), Rental.DATE_TIME_FORMATTER);
				String username = values[1];
				String vehicleId = values[2];
				Integer startLocationX = Integer.parseInt(values[3].replace("\"", ""));
				Integer startLocationY = Integer.parseInt(values[4].replace("\"", ""));
				Integer endLocationX = Integer.parseInt(values[5].replace("\"", ""));
				Integer endLocationY = Integer.parseInt(values[6].replace("\"", ""));
				Integer duration = Integer.parseInt(values[7]);
				boolean breakdown = "da".equalsIgnoreCase(values[8]);
				boolean promotion = "da".equalsIgnoreCase(values[9]);
				rentals.add(new Rental(username, vehicleId, new Field(startLocationX, startLocationY),
						new Field(endLocationX, endLocationY), startDate, duration, promotion, breakdown));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rentals;
	}
}
