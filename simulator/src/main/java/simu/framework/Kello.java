package simu.framework;

/**
 * Singleton class representing the clock in the simulation.
 */
public class Kello {

	private double aika;
	private static Kello instanssi;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Kello() {
		aika = 0;
	}

	/**
	 * Returns the singleton instance of the clock.
	 *
	 * @return the singleton instance
	 */
	public static Kello getInstance() {
		if (instanssi == null) {
			instanssi = new Kello();
		}
		return instanssi;
	}

	/**
	 * Resets the time to zero.
	 */
	public void resetAika() {
		aika = 0;
	}

	/**
	 * Sets the time.
	 *
	 * @param aika the time to set
	 */
	public void setAika(double aika) {
		this.aika = aika;
	}

	/**
	 * Gets the current time.
	 *
	 * @return the current time
	 */
	public double getAika() {
		return aika;
	}
}