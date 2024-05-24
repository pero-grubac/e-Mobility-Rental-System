package net.etfbl.pj2.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.etfbl.pj2.resources.AppConfig;

public class ReportFileManager {

	public static void saveReport(DailyReport report) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		String currentDateAndTime = sdf.format(new Date());
		AppConfig conf = new AppConfig();

		String fileName = conf.getDailyReportFileName() + conf.getReportDocType();
		String filePath = conf.getReportFolder() + File.separator + fileName;
		File reportFolder = new File(conf.getReportFolder());
		if (!reportFolder.exists()) {
			reportFolder.mkdir();
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			oos.writeObject(report);
			System.out.println("DailyReport saved successfully to: " + filePath);
		} catch (IOException e) {
			System.err.println("Error saving DailyReport: " + e.getMessage());
		}
	}

	public static DailyReport readDailyReport() {
		AppConfig conf = new AppConfig();

		String fileName = conf.getDailyReportFileName() + conf.getReportDocType();
		String filePath = conf.getReportFolder() + File.separator + fileName;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			return (DailyReport) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error loading DailyReport: " + e.getMessage());
			return null;
		}
	}
}
