package net.etfbl.pj2.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.Field;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.model.User;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.statistics.DailyReport;
import net.etfbl.pj2.statistics.ReportFileManager;

public class Util {

	public static String generateUUID(Integer length) {
		return UUID.randomUUID().toString().replace("-", "").substring(0, length);
	}

	public static void populateUser(List<Rental> rentals, List<TransportVehicle> vehicles) {
		Map<String, TransportVehicle> vehicleMap = vehicles.stream().filter(vehicle -> vehicle instanceof Car)
				.collect(Collectors.toMap(TransportVehicle::getId, Function.identity()));

		rentals.stream().filter(rental -> vehicleMap.containsKey(rental.getVehicleId())).map(Rental::getUser)
				.filter(Objects::nonNull).forEach(User::generateDocumentation);
	}

	private static boolean isValid(int x, int y, int n, int m) {
		return (x >= 0 && x < n && y >= 0 && y < m);
	}

	public static List<Field> calculateShortesPath(Field start, Field end) {
		AppConfig conf = new AppConfig();
		int n = conf.getTableXMax() + 1;
		int m = conf.getTableYMax() + 1;

		Queue<Field> queue = new LinkedList<Field>();
		queue.offer(start);

		boolean[][] visited = new boolean[n][m];

		Field[][] prev = new Field[n][m];

		while (!queue.isEmpty()) {
			Field current = queue.poll();
			int x = current.getX();
			int y = current.getY();

			// kraj
			if (x == end.getX() && y == end.getY())
				break;

			if (visited[x][y])
				continue;

			visited[x][y] = true;

			// Mogući koraci u četiri smjera: gore, dolje, lijevo, desno
			int[] dx = { -1, 1, 0, 0 };
			int[] dy = { 0, 0, -1, 1 };

			for (int i = 0; i < 4; i++) {
				int newX = x + dx[i];
				int newY = y + dy[i];

				// Provjeravamo da li je novi korak validan i ne posjećen
				if (isValid(newX, newY, n, m) && !visited[newX][newY]) {
					queue.offer(new Field(newX, newY));
					prev[newX][newY] = current;
				}
			}
		}
		List<Field> shortestPath = new ArrayList<Field>();
		Field temp = end;
		while (temp != null) {
			shortestPath.add(temp);
			temp = prev[temp.getX()][temp.getY()];
		}
		Collections.reverse(shortestPath);
		/*
		 * System.out.println("Najkraća putanja iz " + start + " do " + end + ":"); for
		 * (Field f : shortestPath) System.out.println(f);
		 * System.out.println(shortestPath.size());
		 */
		return shortestPath;
	}

	public static List<Invoice> calculateInvoice(List<Rental> rentals, List<TransportVehicle> vehicles) {
		Map<String, TransportVehicle> map = vehicles.stream()
				.collect(Collectors.toMap(TransportVehicle::getId, vehicle -> vehicle, (v1, v2) -> v1));
		List<Invoice> invoices = rentals.stream().map(rental -> new Invoice(rental, map.get(rental.getVehicleId())))
				.collect(Collectors.toList());

		AppConfig conf = new AppConfig();
		// invoices.forEach(invoice ->System.out.println(invoice));
		return invoices;
	}

	public static void generateDailyReports(List<Invoice> invoices) {
		Map<LocalDate, List<Invoice>> dailyInvoices = groupeInvoicesByDate(invoices);

		for (Map.Entry<LocalDate, List<Invoice>> entry : dailyInvoices.entrySet()) {
			LocalDate date = entry.getKey();
			List<Invoice> invoicesForDay = entry.getValue();
			DailyReport dailyReport = new DailyReport(invoicesForDay);
			ReportFileManager.saveReportToTextFile(dailyReport, date);
		}

	}

	public static Map<LocalDate, List<Invoice>> groupeInvoicesByDate(List<Invoice> invoices) {
		return invoices.stream().collect(Collectors.groupingBy(
				invoice -> invoice.getRental().getStartTime().toLocalDate(), TreeMap::new, Collectors.toList()));
	}

	public static Map<LocalDateTime, List<Invoice>> groupeInvoicesByTime(List<Invoice> invoices) {
		return invoices.stream().collect(Collectors.groupingBy(invoice -> invoice.getRental().getStartTime(),
				TreeMap::new, Collectors.toList()));
	}

	public static boolean isNarrowArea(int x, int y) {
		AppConfig conf = new AppConfig();
		if (x < conf.getNarrowBeginingXAxis() || x > conf.getNarrowEndXAxis() || y < conf.getNarrowBeginingYAxis()
				|| y > conf.getNarrowEndYAxis())
			return false;
		return true;
	}
}
