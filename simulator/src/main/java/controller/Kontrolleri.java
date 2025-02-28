package controller;


import javafx.application.Platform;
import simu.framework.IMoottori;
import simu.model.OmaMoottori;


public class Kontrolleri implements IKontrolleriForM, IKontrolleriForV {

    private IMoottori moottori;
    private SimulaattorinController ui;

    public Kontrolleri() {
        this.ui = (SimulaattorinController) ui;
    }

    @Override
    public void kaynnistaSimulointi() {
        moottori = new OmaMoottori();
        moottori.setSimulointiaika(ui.getAika());
        moottori.setViive(ui.getViive());
        ui.tyhjennaVisualisointi();
        ((Thread) moottori).start();
    }

    @Override
    public void hidasta() {
        moottori.setViive((long) (moottori.getViive() * 1.10));
    }

    @Override
    public void nopeuta() {
        moottori.setViive((long) (moottori.getViive() * 0.9));
    }

    @Override
    public void naytaLoppuaika(double aika) {
        Platform.runLater(() -> ui.setLoppuaika(aika));
    }

    @Override
    public void visualisoiAsiakas() {
        Platform.runLater(() -> ui.uusiAsiakas());
    }


    @Override
    public double getTotalEarnings() {
        return 1000.50;
    }

    @Override
    public int getServedRegularCars() {
        return 15;
    }

    @Override
    public int getServedElectricCars() {
        return 10;
    }

    @Override
    public int getRejectedCustomers() {
        return 5;
    }
}



