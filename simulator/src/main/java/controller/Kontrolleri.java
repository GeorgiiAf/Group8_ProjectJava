package controller;

import simu.model.OmaMoottori;

public class Kontrolleri implements IKontrolleriForV {

    private OmaMoottori moottori;
    private double simulointiAika;
    private long viive;

    private double totalEarnings;
    private int servedRegularCars;
    private int servedElectricCars;
    private int rejectedCustomers;

    public Kontrolleri() {
        moottori = new OmaMoottori();
    }

    @Override
    public void kaynnistaSimulointi() {
        moottori.setSimulointiAika(simulointiAika);
        moottori.setViive(viive);

        Thread simulointiThread = new Thread(() -> {
            moottori.aja();
            updateResults();
        });

        simulointiThread.start();
    }

    private void updateResults() {

        this.totalEarnings = moottori.calculateTotalEarnings();
        this.servedRegularCars = moottori.getServedRegularCars();
        this.servedElectricCars = moottori.getServedElectricCars();
        this.rejectedCustomers = moottori.getRejectedCustomers();
    }

    @Override
    public void nopeuta() {
        moottori.setViive((long)(moottori.getViive() * 0.5));
    }

    @Override
    public void hidasta() {
        moottori.setViive((long)(moottori.getViive() * 1.5));
    }

    public void setSimulointiAika(double aika) {
        this.simulointiAika = aika;
    }

    public void setViive(long viive) {
        this.viive = viive;
    }

    @Override
    public double getTotalEarnings() {
        return totalEarnings;
    }

    @Override
    public int getServedRegularCars() {
        return servedRegularCars;
    }

    @Override
    public int getServedElectricCars() {
        return servedElectricCars;
    }

    @Override
    public int getRejectedCustomers() {
        return rejectedCustomers;
    }

    @Override
    public void setTotalEarnings(double earnings) {
        this.totalEarnings = earnings;
    }

    @Override
    public void setServedRegularCars(int count) {
        this.servedRegularCars = count;
    }

    @Override
    public void setServedElectricCars(int count) {
        this.servedElectricCars = count;
    }

    @Override
    public void setRejectedCustomers(int count) {
        this.rejectedCustomers = count;
    }
}