package simu.framework;

import java.util.PriorityQueue;

/**
 * Represents a list of events in the simulation, managed as a priority queue.
 */
public class Tapahtumalista {
    private PriorityQueue<Tapahtuma> lista = new PriorityQueue<>();

    /**
     * Constructs a new Tapahtumalista instance.
     */
    public Tapahtumalista() {
    }

    /**
     * Removes and returns the next event from the list.
     *
     * @return the next event
     */
    public Tapahtuma poista() {
        Trace.out(Trace.Level.INFO, "Tapahtumalistasta poisto " + lista.peek().getTyyppi() + " " + lista.peek().getAika());
        return lista.remove();
    }

    /**
     * Adds a new event to the list.
     *
     * @param t the event to add
     */
    public void lisaa(Tapahtuma t) {
        Trace.out(Trace.Level.INFO, "Tapahtumalistaan lisätään uusi " + t.getTyyppi() + " " + t.getAika());
        lista.add(t);
    }

    /**
     * Gets the time of the next event in the list.
     *
     * @return the time of the next event, or Double.MAX_VALUE if the list is empty
     */
    public double getSeuraavanAika() {
        if (lista.isEmpty()) {
            return Double.MAX_VALUE;   // Avoid null-exception when queue is empty
        }
        return lista.peek().getAika();
    }
}