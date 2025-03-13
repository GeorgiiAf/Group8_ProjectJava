package simu.framework;

import simu.model.Asiakas;

/**
 * Represents an event in the simulation.
 */
public class Tapahtuma implements Comparable<Tapahtuma> {
	private Asiakas asiakas;
	private ITapahtumanTyyppi tyyppi;
	private double aika;

	/**
	 * Constructs a new event.
	 *
	 * @param tyyppi the type of the event
	 * @param aika the time of the event
	 * @param asiakas the customer associated with the event
	 */
	public Tapahtuma(ITapahtumanTyyppi tyyppi, double aika, Asiakas asiakas) {
		this.tyyppi = tyyppi;
		this.aika = aika;
		this.asiakas = asiakas;
	}

	/**
	 * Sets the type of the event.
	 *
	 * @param tyyppi the type of the event
	 */
	public void setTyyppi(ITapahtumanTyyppi tyyppi) {
		this.tyyppi = tyyppi;
	}

	/**
	 * Gets the type of the event.
	 *
	 * @return the type of the event
	 */
	public ITapahtumanTyyppi getTyyppi() {
		return tyyppi;
	}

	/**
	 * Sets the time of the event.
	 *
	 * @param aika the time of the event
	 */
	public void setAika(double aika) {
		this.aika = aika;
	}

	/**
	 * Gets the time of the event.
	 *
	 * @return the time of the event
	 */
	public double getAika() {
		return aika;
	}

	/**
	 * Gets the customer associated with the event.
	 *
	 * @return the customer associated with the event
	 */
	public Asiakas getAsiakas() {
		return asiakas;
	}

	/**
	 * Sets the customer associated with the event.
	 *
	 * @param asiakas the customer to set
	 */
	public void setAsiakas(Asiakas asiakas) {
		this.asiakas = asiakas;
	}

	/**
	 * Compares this event to another event based on their times.
	 *
	 * @param arg the event to compare to
	 * @return -1 if this event is earlier, 1 if later, 0 if they are at the same time
	 */
	@Override
	public int compareTo(Tapahtuma arg) {
		if (this.aika < arg.aika) return -1;
		else if (this.aika > arg.aika) return 1;
		return 0;
	}
}