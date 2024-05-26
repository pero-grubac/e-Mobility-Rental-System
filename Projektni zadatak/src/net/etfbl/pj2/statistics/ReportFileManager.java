package net.etfbl.pj2.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.etfbl.pj2.resources.AppConfig;

public class ReportFileManager {
	private static AppConfig conf = new AppConfig();

	public static void saveReportToBinaryFile(SpecialReport report) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		String currentDateAndTime = sdf.format(new Date());

		String fileName = conf.getSpecialReportFileName() + conf.getReportDocType();
		String filePath = conf.getReportFolder() + File.separator + fileName;
		File reportFolder = new File(conf.getReportFolder());
		if (!reportFolder.exists()) {
			reportFolder.mkdir();
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			oos.writeObject(report);
			System.out.println("SpecialReport saved successfully to: " + filePath);
		} catch (IOException e) {
			System.err.println("Error saving DailyReport: " + e.getMessage());
		}
	}

	public static SpecialReport loadReportFromBinaryFile() {

		String fileName = conf.getSpecialReportFileName() + conf.getReportDocType();
		String filePath = conf.getReportFolder() + File.separator + fileName;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			return (SpecialReport) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error loading DailyReport: " + e.getMessage());
			return null;
		}
	}

	public static void saveReportToTextFile(DailyReport report, LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String reportDate = formatter.format(date);

		String name = (report instanceof SummaryReport) ? conf.getSummaryReportFileName()
				: conf.getDailyReportFileName();

		String fileName = name + " - " + reportDate + conf.getReportDocType();
		String filePath = conf.getReportFolder() + File.separator + fileName;
		File reportFolder = new File(conf.getReportFolder());
		if (!reportFolder.exists()) {
			reportFolder.mkdir();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(report.toString());
			System.out.println(report.getClass().getSimpleName() + " saved successfully to: " + filePath);
		} catch (IOException e) {
			System.err.println("Error saving DailyReport: " + e.getMessage());
		}
	}

	public static List<String> loadReportFromTextFile(DailyReport report) {
		String name = (report instanceof SummaryReport) ? conf.getSummaryReportFileName()
				: conf.getDailyReportFileName();
		File folder = new File(conf.getReportFolder());
		File[] files = folder.listFiles((dir, fileName) -> fileName.contains(name));
		List<String> reportTexts = new ArrayList<>();

		if (files != null) {
			for (File file : files) {
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					StringBuilder reportContent = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						reportContent.append(line).append("\n");
					}
					reportTexts.add(reportContent.toString());
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		return reportTexts;
	}
}
