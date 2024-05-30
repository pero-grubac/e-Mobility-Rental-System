package net.etfbl.pj2.simulation;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.etfbl.pj2.gui.MainFrame;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

public class Simulation {
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	public void startStimulationWithSemaphore(List<Invoice> invoices, AppConfig conf, MainFrame mainframe) {
		Map<LocalDate, List<Invoice>> groupedInvoices = Util.groupeInvoicesByDate(invoices);
		long pause = (long) conf.getPauseBetweenDays() * 1000;

		groupedInvoices.forEach((date, invoiceList) -> {
			mainframe.getLblDate().setText("Date: " + date.format(DATE_FORMATTER));

			Map<LocalDateTime, List<Invoice>> groupedInvoiceByTime = Util.groupeInvoicesByTime(invoiceList);
			groupedInvoiceByTime.forEach((time, list) -> {
				mainframe.getLblTime().setText("Time: " + time.format(TIME_FORMATTER));

				List<Invoice> sortedInvoices = list.stream()
						.sorted(Comparator.comparing(i -> i.getRental().getStartTime())).collect(Collectors.toList());
				Semaphore semaphore = new Semaphore(sortedInvoices.size());
				List<Thread> threads = new ArrayList<>();

				for (Invoice inv : sortedInvoices) {
					Thread thread = new Thread(() -> {
						try {
							semaphore.acquire();
							processInvoice(inv, mainframe);

						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							System.err.println("Thread interrupted: " + e.getMessage());
						} finally {
							semaphore.release();
						}
					});
					threads.add(thread);
					thread.start();
				}

				for (Thread thread : threads) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						System.err.println("Thread interrupted while waiting for completion: " + e.getMessage());
					}
				}
				try {
					System.out.println("pauza");
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Thread interrupted: " + e.getMessage());
				}
			});

		});
	}

	public void startSimulationWithCompletableFuture(List<Invoice> invoices, AppConfig conf, MainFrame mainFrame) {
		Map<LocalDate, List<Invoice>> groupedInvoices = Util.groupeInvoicesByDate(invoices);
		long pause = (long) conf.getPauseBetweenDays() * 1000;
		groupedInvoices.forEach((date, invoiceList) -> {
			System.out.println("Processing rentals for date: " + date);

			Map<LocalDateTime, List<Invoice>> groupedInvoiceByTime = Util.groupeInvoicesByTime(invoiceList);
			groupedInvoiceByTime.forEach((time, list) -> {
				System.out.println("Processing rentals for time: " + time.format(TIME_FORMATTER));

				List<Invoice> sortedInvoices = list.stream()
						.sorted(Comparator.comparing(i -> i.getRental().getStartTime())).collect(Collectors.toList());
				List<CompletableFuture<Void>> futures = new ArrayList<>();
				for (Invoice inv : list) {
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processInvoice(inv, mainFrame));

					futures.add(future);
				}
				CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
				try {
					allFutures.get();
				} catch (InterruptedException | ExecutionException e) {
					System.err.println("Error waiting for completion of invoices: " + e.getMessage());
				}
				try {
					System.out.println("pauza");
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Thread interrupted: " + e.getMessage());
				}
			});

		});
	}

	private static void processInvoice(Invoice invoice, MainFrame mainFrame) {
		Rental rental = invoice.getRental();
		double driveTimePerUnit = 1.0 * rental.getDurationInSeconds() / rental.getShortestPath().size();
		long pause = (long) (driveTimePerUnit * 1000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");
		invoice.setStartBatteryLevel(invoice.getVehicle().getBatteryLevel());

		rental.getShortestPath().forEach(field -> {
			invoice.getVehicle().drainBattery();

			String pervious = mainFrame.getLblVehicle()[field.getY()][field.getX()].getText();
			String id = pervious + " " + invoice.getVehicle().getId();
			mainFrame.getLblVehicle()[field.getY()][field.getX()].setText(id);
			if (invoice.getVehicle() instanceof Car)
				mainFrame.getPnlBoard()[field.getY()][field.getX()].setBackground(mainFrame.getCarColor());
			else if (invoice.getVehicle() instanceof ElectricBike)
				mainFrame.getPnlBoard()[field.getY()][field.getX()].setBackground(mainFrame.getBikeColor());
			else if (invoice.getVehicle() instanceof ElectricScooter)
				mainFrame.getPnlBoard()[field.getY()][field.getX()].setBackground(mainFrame.getScooterColor());
			/*
			 * System.out.println(field + " " + rental.getVehicleId() + " batteryLevel: " +
			 * String.format("%.2f", invoice.getVehicle().getBatteryLevel()) + " time: " +
			 * LocalDateTime.now().format(formatter));
			 */
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Thread interrupted: " + e.getMessage());
			}
			if (Util.isNarrowArea(field.getX(), field.getY()))
				mainFrame.getPnlBoard()[field.getY()][field.getX()].setBackground(mainFrame.getNarrowColor());
			else
				mainFrame.getPnlBoard()[field.getY()][field.getX()].setBackground(mainFrame.getWideColor());

			pervious = mainFrame.getLblVehicle()[field.getY()][field.getX()].getText();
			mainFrame.getLblVehicle()[field.getY()][field.getX()]
					.setText(pervious.replaceAll(Pattern.quote(invoice.getVehicle().getId()), "").trim());

			invoice.setAfterbatteryLevel(invoice.getVehicle().getBatteryLevel());
			rental.setEndTime(rental.getStartTime().plusSeconds(rental.getDurationInSeconds()));
		});
	}

}
