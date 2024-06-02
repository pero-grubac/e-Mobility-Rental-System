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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.etfbl.pj2.resources.AppConfig;

/**
 * Manages saving and loading of various types of reports.
 * 
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class ReportFileManager {
	private static AppConfig conf = new AppConfig();

	/**
	 * Saves a special report to a binary file.
	 *
	 * @param report The special report to save.
	 */

	public static void saveReportToBinaryFile(SpecialReport report) {
		try {
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
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Loads a special report from a binary file.
	 *
	 * @return The loaded special report.
	 */
	public static SpecialReport loadReportFromBinaryFile() {

		try {
			String fileName = conf.getSpecialReportFileName() + conf.getReportDocType();
			String filePath = conf.getReportFolder() + File.separator + fileName;
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
				return (SpecialReport) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Error loading DailyReport: " + e.getMessage());
				return null;
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Saves a daily report to a text file.
	 *
	 * @param report The daily report to save.
	 * @param date   The date of the report.
	 */
	public static void saveReportToTextFile(DailyReport report, LocalDate date) {
		try {
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
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Loads daily reports from text files.
	 *
	 * @param report The type of report to load (either DailyReport or
	 *               SummaryReport).
	 * @return A map containing report text content.
	 */
	public static Map<String, String> loadReportFromTextFile(DailyReport report) {
		Map<String, String> reportTexts = new HashMap<>();
		try {
			String name = (report instanceof SummaryReport) ? conf.getSummaryReportFileName()
					: conf.getDailyReportFileName();
			File folder = new File(conf.getReportFolder());
			File[] files = folder.listFiles((dir, fileName) -> fileName.contains(name));

			if (files != null) {
				for (File file : files) {
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						StringBuilder reportContent = new StringBuilder();
						String line;
						while ((line = br.readLine()) != null) {
							reportContent.append(line).append("\n");
						}
						reportTexts.put(file.getName().substring(0, file.getName().lastIndexOf('.')),
								reportContent.toString());
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return reportTexts;
	}
}
