package net.etfbl.pj2.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
	private Properties properties;

    public AppConfig() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find app.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public double getDoubleProperty(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    public double getCarUnitPrice() {
        return getDoubleProperty("CAR_UNIT_PRICE");
    }

    public double getBikeUnitPrice() {
        return getDoubleProperty("BIKE_UNIT_PRICE");
    }

    public double getScooterUnitPrice() {
        return getDoubleProperty("SCOOTER_UNIT_PRICE");
    }

    public double getDistanceNarrow() {
        return getDoubleProperty("DISTANCE_NARROW");
    }

    public double getDistanceWide() {
        return getDoubleProperty("DISTANCE_WIDE");
    }

    public double getDiscount() {
        return getDoubleProperty("DISCOUNT");
    }

    public double getDiscountProm() {
        return getDoubleProperty("DISCOUNT_PROM");
    }
}
