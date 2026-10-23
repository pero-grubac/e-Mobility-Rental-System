package net.etfbl.pj2.emobility;

import javax.swing.SwingUtilities;

import net.etfbl.pj2.gui.MainFrame;
import net.etfbl.pj2.gui.Theme;
import net.etfbl.pj2.resources.AppConfig;
import net.etfbl.pj2.simulation.Simulation;

/**
 * Entry point of the application. Loads the vehicles and rentals, opens the
 * main window and starts the simulation in the background.
 *
 * @author Pero Grubač
 * @since 2.6.2024.
 */
public class EMobility {

	/**
	 * Starts the application.
	 *
	 * @param args Command line arguments (not used).
	 */
	public static void main(String[] args) {
		AppConfig conf = new AppConfig();
		DataLoader.Data data = DataLoader.load(conf);

		SwingUtilities.invokeLater(() -> {
			Theme.install(conf);
			MainFrame mainFrame = new MainFrame(conf, data.vehicles(), data.invoices());
			mainFrame.setExtendedState(MainFrame.MAXIMIZED_BOTH);
			mainFrame.setVisible(true);

			Thread simulation = new Thread(new Simulation(data.invoices(), conf, mainFrame), "simulation");
			simulation.setDaemon(true);
			simulation.start();
		});
	}
}
