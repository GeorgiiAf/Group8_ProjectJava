package simu.framework;

/**
 * Abstract class representing the engine of the simulation.
 */
public abstract class Moottori {

	private double simulointiaika = 0;
	private Kello kello;
	protected Tapahtumalista tapahtumalista;
	private long viive = 100;
	private boolean paused = false;
	private boolean stopped = false;

	/**
	 * Constructs a new Moottori instance.
	 */
	public Moottori() {
		kello = Kello.getInstance(); // Otetaan kello muuttujaan yksinkertaistamaan koodia
		tapahtumalista = new Tapahtumalista();
		// Palvelupisteet luodaan simu.model-pakkauksessa Moottorin aliluokassa
	}

	/**
	 * Sets the simulation time.
	 *
	 * @param aika the simulation time to set
	 */
	public void setSimulointiaika(double aika) {
		simulointiaika = aika;
	}

	/**
	 * Sets the delay between simulation steps.
	 *
	 * @param viive the delay to set
	 * @throws IllegalArgumentException if the delay is negative
	 */
	public void setViive(long viive) {
		if (viive < 0) {
			throw new IllegalArgumentException("Delay must be non-negative");
		}
		this.viive = viive;
	}

	/**
	 * Gets the delay between simulation steps.
	 *
	 * @return the delay
	 */
	public long getViive() {
		return viive;
	}

	/**
	 * Toggles the pause state of the simulation.
	 */
	public void setPause() {
		paused = !paused;
		if (!paused) {
			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Runs the simulation.
	 */
	public void aja() {
		alustukset(); // luodaan mm. ensimmäinen tapahtuma
		while (simuloidaan() && !stopped) {
			Trace.out(Trace.Level.INFO, "\nA-vaihe: kello on " + nykyaika());
			kello.setAika(nykyaika());

			Trace.out(Trace.Level.INFO, "\nB-vaihe:");
			suoritaBTapahtumat();

			Trace.out(Trace.Level.INFO, "\nC-vaihe:");
			yritaCTapahtumat();

			synchronized (this) {
				try {
					if (paused) {
						wait();
					}
					Thread.sleep(viive > 0 ? viive : 100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		tulokset();
		kello.resetAika();
	}

	/**
	 * Executes B-phase events.
	 */
	private void suoritaBTapahtumat() {
		while (tapahtumalista.getSeuraavanAika() == kello.getAika()) {
			suoritaTapahtuma(tapahtumalista.poista());
		}
	}

	/**
	 * Gets the current time.
	 *
	 * @return the current time
	 */
	private double nykyaika() {
		return tapahtumalista.getSeuraavanAika();
	}

	/**
	 * Checks if the simulation is still running.
	 *
	 * @return true if the simulation is still running, false otherwise
	 */
	private boolean simuloidaan() {
		return kello.getAika() < simulointiaika;
	}

	/**
	 * Stops the simulation.
	 *
	 * @return the new stopped state
	 */
	public boolean stopSimulation() {
		return stopped = !stopped;
	}

	/**
	 * Executes the given event.
	 *
	 * @param t the event to execute
	 */
	protected abstract void suoritaTapahtuma(Tapahtuma t);

	/**
	 * Attempts to execute C-phase events.
	 */
	protected abstract void yritaCTapahtumat();

	/**
	 * Initializes the simulation.
	 */
	protected abstract void alustukset();

	/**
	 * Outputs the results of the simulation.
	 */
	protected abstract void tulokset();

	/**
	 * Gets the clock instance.
	 *
	 * @return the clock instance
	 */
	public Kello getKello() {
		return Kello.getInstance();
	}
}