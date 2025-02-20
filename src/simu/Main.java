package simu;

import simu.framework.Trace;
import simu.model.OmaMoottori;

public class Main {
    public static void main(String[] args) {
        Trace.setTraceLevel(Trace.Level.INFO);

        OmaMoottori m = new OmaMoottori();
        m.setSimulointiaika(100);
        m.aja();
    }
}