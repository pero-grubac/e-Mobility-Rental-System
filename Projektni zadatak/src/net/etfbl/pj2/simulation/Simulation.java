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
import java.util.stream.Collectors;
import net.etfbl.pj2.model.Field;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.etfbl.pj2.gui.MainFrame;
import net.etfbl.pj2.invoice.Invoice;
import net.etfbl.pj2.model.Car;
import net.etfbl.pj2.model.ElectricBike;
import net.etfbl.pj2.model.ElectricScooter;
import net.etfbl.pj2.model.TransportVehicle;
import net.etfbl.pj2.rental.Rental;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.util.Util;

public class Simulation {
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	public void startStimulationWithSemaphore(List<Invoice> invoices, AppConfig conf, MainFrame mainFrame) {
		Map<LocalDate, List<Invoice>> groupedInvoices = Util.groupeInvoicesByDate(invoices);
		long pause = (long) conf.getPauseBetweenTime() * 1000;

		groupedInvoices.forEach((date, invoiceList) -> {
			mainFrame.getLblDate().setText("Date: " + date.format(DATE_FORMATTER));

			Map<LocalDateTime, List<Invoice>> groupedInvoiceByTime = Util.groupeInvoicesByTime(invoiceList);
			groupedInvoiceByTime.forEach((time, list) -> {
				mainFrame.getLblTime().setText("Time: " + time.format(TIME_FORMATTER));

				List<Invoice> sortedInvoices = list.stream()
						.sorted(Comparator.comparing(i -> i.getRental().getStartTime())).collect(Collectors.toList());
				Semaphore semaphore = new Semaphore(sortedInvoices.size());
				List<Thread> threads = new ArrayList<>();

				for (Invoice inv : sortedInvoices) {
					Thread thread = new Thread(() -> {
						try {
							semaphore.acquire();
							processInvoice(inv, mainFrame, conf);

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
					System.out.println("pause");
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Thread interrupted: " + e.getMessage());
				}
			});

		});
		endMessage();
	}

	public void startSimulationWithCompletableFuture(List<Invoice> invoices, AppConfig conf, MainFrame mainFrame) {
		Map<LocalDate, List<Invoice>> groupedInvoices = Util.groupeInvoicesByDate(invoices);
		long pause = (long) conf.getPauseBetweenTime() * 1000;
		groupedInvoices.forEach((date, invoiceList) -> {
			mainFrame.getLblDate().setText("Date: " + date.format(DATE_FORMATTER));

			Map<LocalDateTime, List<Invoice>> groupedInvoiceByTime = Util.groupeInvoicesByTime(invoiceList);
			groupedInvoiceByTime.forEach((time, list) -> {
				mainFrame.getLblTime().setText("Time: " + time.format(TIME_FORMATTER));

				List<Invoice> sortedInvoices = list.stream()
						.sorted(Comparator.comparing(i -> i.getRental().getStartTime())).collect(Collectors.toList());
				List<CompletableFuture<Void>> futures = new ArrayList<>();
				for (Invoice inv : sortedInvoices) {
					CompletableFuture<Void> future = CompletableFuture
							.runAsync(() -> processInvoice(inv, mainFrame, conf));

					futures.add(future);
				}
				CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
				try {
					allFutures.get();
				} catch (InterruptedException | ExecutionException e) {
					System.err.println("Error waiting for completion of invoices: " + e.getMessage());
				}
				try {
					System.out.println("pause");
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Thread interrupted: " + e.getMessage());
				}
			});

		});
		endMessage();
	}

	private static void processInvoice(Invoice invoice, MainFrame mainFrame, AppConfig conf) {
		Rental rental = invoice.getRental();
		double driveTimePerUnit = 1.0 * rental.getDurationInSeconds() / rental.getShortestPath().size();
		long pause = (long) ((driveTimePerUnit * 1000) > conf.getMaxDriveTimePerUnit() ? conf.getMaxDriveTimePerUnit()
				: driveTimePerUnit * 1000);

		invoice.setStartBatteryLevel(invoice.getVehicle().getBatteryLevel());

		rental.getShortestPath().forEach(field -> {
			invoice.getVehicle().drainBattery();
			addVehicleToField(mainFrame, field, invoice.getVehicle());

			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Thread interrupted: " + e.getMessage());
			}

			removeVehicleFromField(mainFrame, field, invoice.getVehicle());

			invoice.setAfterbatteryLevel(invoice.getVehicle().getBatteryLevel());
			rental.setEndTime(rental.getStartTime().plusSeconds(rental.getDurationInSeconds()));
		});
		// TODO sacuvaj ivoice
	}

	private static void addVehicleToField(MainFrame mainFrame, Field field, TransportVehicle vehicle) {
		SwingUtilities.invokeLater(() -> {
			List<TransportVehicle> vehicleList = mainFrame.getVehicles()[field.getY()][field.getX()];
			vehicleList.add(vehicle);

			updateLabelAndPanel(mainFrame, field, vehicleList);
		});
	}

	private static void removeVehicleFromField(MainFrame mainFrame, Field field, TransportVehicle vehicle) {
		SwingUtilities.invokeLater(() -> {
			List<TransportVehicle> vehicleList = mainFrame.getVehicles()[field.getY()][field.getX()];
			vehicleList.remove(vehicle);

			updateLabelAndPanel(mainFrame, field, vehicleList);
		});
	}

	private static void updateLabelAndPanel(MainFrame mainFrame, Field field, List<TransportVehicle> vehicleList) {
		JPanel panel = mainFrame.getPnlBoard()[field.getY()][field.getX()];
		JLabel label = mainFrame.getLblVehicle()[field.getY()][field.getX()];

		String text = vehicleList.stream().map(v -> v.getId() + "(" + String.format("%.0f", v.getBatteryLevel()) + "%)")
				.collect(Collectors.joining(" "));
		label.setText(text);

		if (vehicleList.isEmpty()) {
			panel.setBackground(Util.isNarrowArea(field.getX(), field.getY()) ? mainFrame.getNarrowColor()
					: mainFrame.getWideColor());
		} else {
			panel.setBackground(getColorForVehicle(vehicleList.get(0), mainFrame));
		}
	}

	private static Color getColorForVehicle(TransportVehicle vehicle, MainFrame mainFrame) {
		if (vehicle instanceof Car) {
			return mainFrame.getCarColor();
		} else if (vehicle instanceof ElectricBike) {
			return mainFrame.getBikeColor();
		} else if (vehicle instanceof ElectricScooter) {
			return mainFrame.getScooterColor();
		}
		return mainFrame.getWideColor(); 
	}
	
	private static void endMessage() {
		JOptionPane.showMessageDialog(null, "END");
	}
}
