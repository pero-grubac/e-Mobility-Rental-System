package net.etfbl.pj2.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.TransportVehicle;

public class VehicleParser {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy.");

	public List<TransportVehicle> parseVehicles(String filePath) {
		List<TransportVehicle> vehicles = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			// preskociti zaglavlje
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				String id = values[0];
				String manufacturer = values[1];
				String model = values[2];
                LocalDate purchaseDate = values[3].isEmpty() ? null : LocalDate.parse(values[3], DATE_FORMATTER);
				Double purchasePrice = values[4].isEmpty() ? 0 : Double.parseDouble(values[4]);
				Integer range = values[5].isEmpty() ? 0 : Integer.parseInt(values[5]);
				Integer maxSpeed = values[6].isEmpty() ? 0 : Integer.parseInt(values[6]);
				String description = values[7];
				String type = values[8];
				
				TransportVehicle vehicle = null;
				switch (type.toLowerCase()) {
                case "automobil":
                    vehicle = new Car(id,manufacturer,model,purchasePrice,purchaseDate,description); 
                    break;
                case "bicikl":
                   // vehicle = new Bike(id, manufacturer, model, purchasePrice, 100, range); // Battery level is set to 100 as an example
                    break;
                case "trotinet":
                   // vehicle = new Scooter(id, manufacturer, model, purchasePrice, 100, maxSpeed); // Battery level is set to 100 as an example
                    break;
            }
            vehicles.add(vehicle);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vehicles;
	}
}
