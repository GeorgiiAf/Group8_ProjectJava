package simu.framework;

import eduni.distributions.*;
import simu.model.Asiakas;

/**
 * Represents the arrival process in the simulation.
 */
public class Saapumisprosessi {

	private ContinuousGenerator generaattori;
	private Tapahtumalista tapahtumalista;
	private ITapahtumanTyyppi tyyppi;

	/**
	 * Constructs a new arrival process.
	 *
	 * @param g the generator for continuous random variables
	 * @param tl the event list
	 * @param tyyppi the type of the event
	 */
	public Saapumisprosessi(ContinuousGenerator g, Tapahtumalista tl, ITapahtumanTyyppi tyyppi) {
		this.generaattori = g;
		this.tapahtumalista = tl;
		this.tyyppi = tyyppi;
	}

	/**
	 * Generates the next event in the arrival process.
	 *
	 * @param asiakas the customer associated with the event
	 */
	public void generoiSeuraava(Asiakas asiakas) {
		Trace.out(Trace.Level.INFO, "Generoi seuraava");
		Tapahtuma t = new Tapahtuma(tyyppi, Kello.getInstance().getAika() + generaattori.sample(), asiakas);
		tapahtumalista.lisaa(t);
	}
}