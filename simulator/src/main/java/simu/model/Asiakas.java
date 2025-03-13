package simu.model;

import simu.framework.*;

/**
 * Represents a customer in the simulation.
 */
public class Asiakas {
	private double saapumisaika;
	private double poistumisaika;
	private final int id;
	public static int i = 1;
	private static double sum = 0;

	private final boolean isElectricCar;
	private final boolean isNoPartsNeeded;

	private final double defaultCost;
	private final double electricCost;
	private final double partsCost;

	private int currentQueueIndex; // Where this customer is

	/**
	 * Constructs a new customer.
	 *
	 * @param isElectricCar whether the customer has an electric car
	 * @param isNoPartsNeeded whether the customer needs no parts
	 * @param defaultCost the default cost of service
	 * @param electricCost the cost for electric cars
	 * @param partsCost the cost for parts
	 */
	public Asiakas(boolean isElectricCar, boolean isNoPartsNeeded, double defaultCost, double electricCost, double partsCost) {
		this.isElectricCar = isElectricCar;
		this.isNoPartsNeeded = isNoPartsNeeded;
		this.defaultCost = defaultCost;
		this.electricCost = electricCost;
		this.partsCost = partsCost;
		id = i++;

		saapumisaika = Kello.getInstance().getAika();
		Trace.out(Trace.Level.INFO, "Uusi asiakas nro " + id + " saapui klo " + saapumisaika);
	}

	/**
	 * Gets the current queue index.
	 *
	 * @return the current queue index
	 */
	public int getCurrentQueueIndex() {
		return currentQueueIndex;
	}

	/**
	 * Sets the current queue index.
	 *
	 * @param currentQueueIndex the current queue index to set
	 */
	public void setCurrentQueueIndex(int currentQueueIndex) {
		this.currentQueueIndex = currentQueueIndex;
	}

	/**
	 * Gets the departure time.
	 *
	 * @return the departure time
	 */
	public double getPoistumisaika() {
		return poistumisaika;
	}

	/**
	 * Sets the departure time.
	 *
	 * @param poistumisaika the departure time to set
	 */
	public void setPoistumisaika(double poistumisaika) {
		this.poistumisaika = poistumisaika;
	}

	/**
	 * Gets the arrival time.
	 *
	 * @return the arrival time
	 */
	public double getSaapumisaika() {
		return saapumisaika;
	}

	/**
	 * Sets the arrival time.
	 *
	 * @param saapumisaika the arrival time to set
	 */
	public void setSaapumisaika(double saapumisaika) {
		this.saapumisaika = saapumisaika;
	}

	/**
	 * Checks if the customer has an electric car.
	 *
	 * @return true if the customer has an electric car, false otherwise
	 */
	public boolean isElectricCar() {
		return this.isElectricCar;
	}

	/**
	 * Checks if the customer needs no parts.
	 *
	 * @return true if the customer needs no parts, false otherwise
	 */
	public boolean isNoPartsNeeded() {
		return this.isNoPartsNeeded;
	}

	/**
	 * Gets the customer ID.
	 *
	 * @return the customer ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Prints a report of the customer's service.
	 *
	 * @return the report as a string
	 */
	public String raportti() {
		String report = "\nAsiakas " + id + " valmis!\n"
				+ "Asiakas " + id + " saapui: " + saapumisaika + "\n"
				+ "Asiakas " + id + " poistui: " + poistumisaika + "\n"
				+ "Asiakas " + id + " viipyi: " + (poistumisaika - saapumisaika);

		Trace.out(Trace.Level.INFO, report);
		System.out.println(report);

		sum += (poistumisaika - saapumisaika);
		double keskiarvo = sum / id;
		String avgTimeMsg = "Asiakkaiden läpimenoaikojen keskiarvo tähän asti " + keskiarvo;
		System.out.println(avgTimeMsg);

		return report + "\n" + avgTimeMsg;
	}

	/**
	 * Calculates the service cost for the customer.
	 *
	 * @return the total service cost
	 */
	public double calculateServiceCost() {
		double baseCost = 0.0;

		if (isElectricCar()) {
			baseCost += electricCost;
		} else {
			baseCost += defaultCost;
		}

		if (!isNoPartsNeeded()) {
			baseCost += partsCost;
		}

		double timeSpent = getPoistumisaika() - getSaapumisaika();
		baseCost += timeSpent * 2.0;

		return baseCost;
	}

	/**
	 * Checks if the service for the customer is completed.
	 *
	 * @return true if the service is completed, false otherwise
	 */
	public boolean isServiceCompleted() {
		return poistumisaika > 0;
	}
}