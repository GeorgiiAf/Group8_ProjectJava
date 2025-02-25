package simu;

import controller.IKontrolleriForM;
import controller.Kontrolleri;
import simu.framework.Trace;
import simu.model.OmaMoottori;
import controller.SimulaattorinController;
import view.SimulaattorinGUI;


public class Main {
    public static void main(String[] args) {
        Trace.setTraceLevel(Trace.Level.INFO);
        SimulaattorinController ui = new SimulaattorinController(); // Создаем UI
        IKontrolleriForM kontrolleri = new Kontrolleri();

        OmaMoottori m = new OmaMoottori(kontrolleri);
        m.setSimulointiaika(100);
        m.start();
        SimulaattorinGUI.launch(SimulaattorinGUI.class);

    }
}