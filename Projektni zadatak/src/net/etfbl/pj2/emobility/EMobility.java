package net.etfbl.pj2.emobility;

import java.io.File;
import java.util.List;

import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.parser.VehicleParser;

public class EMobility {
	public static void main(String[] args) {
        String filePath = "Test" + File.separator + "PJ2 - projektni zadatak 2024 - Prevozna sredstva.csv";
        VehicleParser vehicleParser = new VehicleParser();
        List<TransportVehicle> vehicles = vehicleParser.parseVehicles(filePath);
        for (TransportVehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
	}
}
