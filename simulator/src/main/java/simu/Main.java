package simu;


import simu.framework.Trace;
import simu.model.OmaMoottori;
import controller.SimulaattorinController;
import view.SimulaattorinGUI;


public class Main {
    public static void main(String[] args) {
        Trace.setTraceLevel(Trace.Level.INFO);
        OmaMoottori m = new OmaMoottori();
        SimulaattorinController controller;
        controller = new SimulaattorinController();
        controller.setMoottori(m);

        m.setSimulointiaika(10000);
        m.aja();
        SimulaattorinGUI.launch(SimulaattorinGUI.class);

    }
}