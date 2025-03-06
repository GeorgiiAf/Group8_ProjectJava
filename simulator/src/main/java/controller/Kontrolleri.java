package controller;
//  REWORK THIS
import simu.model.OmaMoottori;

public class Kontrolleri implements IKontrolleriForV {

    private final OmaMoottori moottori;

    private double totalEarnings;
    private int servedRegularCars;
    private int servedElectricCars;
    private int rejectedCustomers;

    public Kontrolleri() {
        moottori = new OmaMoottori();
    }

    private void updateResults() {
        this.totalEarnings = moottori.calculateTotalEarnings();
        this.servedRegularCars = moottori.getServedRegularCars();
        this.servedElectricCars = moottori.getServedElectricCars();
        this.rejectedCustomers = moottori.getRejectedCustomers();
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