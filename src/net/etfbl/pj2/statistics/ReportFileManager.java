package net.etfbl.pj2.statistics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.etfbl.pj2.resources.AppConfig;

/**
 * Saves reports to files and reads the serialized special report back.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public final class ReportFileManager {

	private ReportFileManager() {
	}

	/**
	 * Saves a daily report as a text file named after its date.
	 *
	 * @param report The report.
	 * @param conf   The application configuration.
	 * @return The written file.
	 * @throws IOException If the file cannot be written.
	 */
	public static Path saveDailyReport(DailyReport report, AppConfig conf) throws IOException {
		LocalDate date = report.getDate();
		String name = conf.getDailyReportFileName() + " - " + date.format(conf.getFileDateFormatter());
		return writeText(name, "Daily report " + date.format(conf.getFileDateFormatter()), report, conf);
	}

	/**
	 * Saves the summary report as a text file.
	 *
	 * @param report The report.
	 * @param conf   The application configuration.
	 * @return The written file.
	 * @throws IOException If the file cannot be written.
	 */
	public static Path saveSummaryReport(SummaryReport report, AppConfig conf) throws IOException {
		return writeText(conf.getSummaryReportFileName(), "Summary report", report, conf);
	}

	private static Path writeText(String name, String title, DailyReport report, AppConfig conf) throws IOException {
		Path folder = Paths.get(conf.getReportFolder());
		Files.createDirectories(folder);
		Path file = folder.resolve(name + conf.getTextFileExtension());
		Files.writeString(file, title + System.lineSeparator() + report.toText(), StandardCharsets.UTF_8);
		return file;
	}

	/**
	 * Serializes every vehicle of the special report, with all its fields and its
	 * revenue, to its own binary file. Files of an earlier run are replaced.
	 *
	 * @param report The special report.
	 * @param conf   The application configuration.
	 * @return The written files.
	 * @throws IOException If a file cannot be written.
	 */
	public static List<Path> saveSpecialReport(SpecialReport report, AppConfig conf) throws IOException {
		Path folder = conf.getSpecialReportFolder();
		Files.createDirectories(folder);
		for (Path old : listBinaryFiles(conf)) {
			Files.delete(old);
		}
		List<Path> files = new ArrayList<>();
		for (VehicleRevenue revenue : report.getTopVehicles()) {
			Path file = folder.resolve(revenue.getVehicle().getType() + " - " + revenue.getVehicle().getId()
					+ conf.getBinaryFileExtension());
			try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
				out.writeObject(revenue);
			}
			files.add(file);
		}
		return files;
	}

	/**
	 * Deserializes all files of the special report.
	 *
	 * @param conf The application configuration.
	 * @return The vehicles and their revenue, by vehicle type; empty if nothing
	 *         has been saved yet.
	 * @throws IOException If a file cannot be read.
	 */
	public static List<VehicleRevenue> loadSpecialReport(AppConfig conf) throws IOException {
		List<VehicleRevenue> revenues = new ArrayList<>();
		for (Path file : listBinaryFiles(conf)) {
			try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
				revenues.add((VehicleRevenue) in.readObject());
			} catch (ClassNotFoundException | ClassCastException e) {
				throw new IOException("Not a special report file: " + file, e);
			}
		}
		revenues.sort(Comparator.comparing(r -> r.getVehicle().getType()));
		return revenues;
	}

	private static List<Path> listBinaryFiles(AppConfig conf) throws IOException {
		Path folder = conf.getSpecialReportFolder();
		List<Path> files = new ArrayList<>();
		if (!Files.isDirectory(folder))
			return files;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*" + conf.getBinaryFileExtension())) {
			stream.forEach(files::add);
		}
		files.sort(Comparator.naturalOrder());
		return files;
	}
}
