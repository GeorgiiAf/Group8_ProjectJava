package simu.framework;

import java.util.PriorityQueue;

public class Tapahtumalista {
    private PriorityQueue<Tapahtuma> lista = new PriorityQueue<>();

    public Tapahtumalista() {

    }

    public Tapahtuma poista() {
        Trace.out(Trace.Level.INFO, "Tapahtumalistasta poisto " + lista.peek().getTyyppi() + " " + lista.peek().getAika());
        return lista.remove();
    }

    public void lisaa(Tapahtuma t) {
        Trace.out(Trace.Level.INFO, "Tapahtumalistaan lisätään uusi " + t.getTyyppi() + " " + t.getAika());
        lista.add(t);
    }

    public double getSeuraavanAika() {
        if (lista.isEmpty()) {
            return Double.MAX_VALUE;   // Avoid null-exception when queue is empty
        }
        return lista.peek().getAika();
    }
}
