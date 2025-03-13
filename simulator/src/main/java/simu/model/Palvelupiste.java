package simu.model;

import simu.framework.*;

import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;

/**
 * Represents a service point in the simulation.
 */
public class Palvelupiste {
    private final int maxEngineers;
    private int availableEngineers;
    private final LinkedList<Asiakas> jono = new LinkedList<>(); // Tietorakennetoteutus
    private final ContinuousGenerator generator;
    private final Tapahtumalista tapahtumalista;
    private final TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;

    private final String type;
    private final int id;

    private static int nextId = 0;

    private boolean varattu = false;
    private double totalBusyTime = 0;
    private double lastStartTime = 0;

    /**
     * Constructs a new service point.
     *
     * @param maxEngineers   the maximum number of engineers
     * @param generator      the service time generator
     * @param tapahtumalista the event list
     * @param tyyppi         the type of event to schedule
     */
    public Palvelupiste(int maxEngineers, ContinuousGenerator generator, Tapahtumalista tapahtumalista, TapahtumanTyyppi tyyppi) {
        this.maxEngineers = maxEngineers;
        this.availableEngineers = maxEngineers;
        this.tapahtumalista = tapahtumalista;
        this.generator = generator;
        this.skeduloitavanTapahtumanTyyppi = tyyppi;

        // Initialize type and ID
        this.type = tyyppi.toString(); // Convert TapahtumanTyyppi to String
        this.id = nextId++;
    }

    /**
     * Adds a customer to the queue.
     *
     * @param a the customer to add
     */
    public void lisaaJonoon(Asiakas a) {
        jono.add(a);
        a.setSaapumisaika(Kello.getInstance().getAika());
        Trace.out(Trace.Level.INFO, "Asiakas " + a.getId() + " lisättiin jonoon palvelupisteeseen " + skeduloitavanTapahtumanTyyppi);
    }

    /**
     * Removes and returns the first customer from the queue.
     *
     * @return the first customer in the queue
     */
    public Asiakas otaJonosta() {
        if (varattu) {
            double endTime = Kello.getInstance().getAika();
            totalBusyTime += (endTime - lastStartTime);
        }
        availableEngineers++;
        varattu = false;
        return jono.poll();
    }

    /**
     * Starts servicing the next customer in the queue.
     */
    public void aloitaPalvelu() {
        Trace.out(Trace.Level.INFO, "Attempting to start service at " + skeduloitavanTapahtumanTyyppi + ". Engineers: " + availableEngineers + ", Queue: " + jono.size());
        if (availableEngineers > 0 && !jono.isEmpty()) {
            availableEngineers--;

            varattu = true;
            lastStartTime = Kello.getInstance().getAika();
            double palveluAika = generator.sample();
            Trace.out(Trace.Level.INFO, "PALVELUAIKA-> " + palveluAika);
            Tapahtuma t = new Tapahtuma(skeduloitavanTapahtumanTyyppi, Kello.getInstance().getAika() + palveluAika, this.jono.getLast());
            tapahtumalista.lisaa(t);
        }
    }

    /**
     * Checks if the service point is busy.
     *
     * @return true if the service point is busy, false otherwise
     */
    public boolean onVarattu() {
        return varattu;
    }

    /**
     * Checks if there are customers in the queue.
     *
     * @return true if there are customers in the queue, false otherwise
     */
    public boolean onJonossa() {
        return !jono.isEmpty();
    }

    /**
     * Gets the size of the queue.
     *
     * @return the size of the queue
     */
    public int getQueueSize() {
        return this.jono.size();
    }

    /**
     * Gets the utilization rate of the service point.
     *
     * @return the utilization rate
     */
    public double getUtilizationRate() {
        double totalTime = Kello.getInstance().getAika();
        return totalBusyTime / totalTime;
    }

    /**
     * Gets the type of the service point.
     *
     * @return the type of the service point
     */
    public String getType() {
        return type;
    }
}