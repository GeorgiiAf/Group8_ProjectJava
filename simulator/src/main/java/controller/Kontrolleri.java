package controller;

import simu.model.OmaMoottori;

/**
 * Controller class for managing the simulation.
 */
public class Kontrolleri implements IKontrolleriForV {

    private final OmaMoottori moottori;
    private double simulointiAika;
    private long viive;

    private double totalEarnings;
    private int servedRegularCars;
    private int servedElectricCars;
    private int rejectedCustomers;

    /**
     * Constructs a new Kontrolleri instance.
     */
    public Kontrolleri() {
        moottori = new OmaMoottori();
    }

    /**
     * Starts the simulation.
     */
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

    /**
     * Updates the simulation results.
     */
    private void updateResults() {
        this.totalEarnings = moottori.calculateTotalEarnings();
        this.servedRegularCars = moottori.getServedRegularCars();
        this.servedElectricCars = moottori.getServedElectricCars();
        this.rejectedCustomers = moottori.getRejectedCustomers();
    }

    /**
     * Speeds up the simulation.
     */
    @Override
    public void nopeuta() {
        moottori.setViive((long)(moottori.getViive() * 0.5));
    }

    /**
     * Slows down the simulation.
     */
    @Override
    public void hidasta() {
        moottori.setViive((long)(moottori.getViive() * 1.5));
    }

    /**
     * Sets the simulation time.
     *
     * @param aika the simulation time
     */
    public void setSimulointiAika(double aika) {
        this.simulointiAika = aika;
    }

    /**
     * Sets the delay for the simulation.
     *
     * @param viive the delay
     */
    public void setViive(long viive) {
        this.viive = viive;
    }

    /**
     * Gets the total earnings from the simulation.
     *
     * @return the total earnings
     */
    @Override
    public double getTotalEarnings() {
        return totalEarnings;
    }

    /**
     * Gets the number of served regular cars.
     *
     * @return the number of served regular cars
     */
    @Override
    public int getServedRegularCars() {
        return servedRegularCars;
    }

    /**
     * Gets the number of served electric cars.
     *
     * @return the number of served electric cars
     */
    @Override
    public int getServedElectricCars() {
        return servedElectricCars;
    }

    /**
     * Gets the number of rejected customers.
     *
     * @return the number of rejected customers
     */
    @Override
    public int getRejectedCustomers() {
        return rejectedCustomers;
    }

    /**
     * Sets the total earnings for the simulation.
     *
     * @param earnings the total earnings
     */
    @Override
    public void setTotalEarnings(double earnings) {
        this.totalEarnings = earnings;
    }

    /**
     * Sets the number of served regular cars.
     *
     * @param count the number of served regular cars
     */
    @Override
    public void setServedRegularCars(int count) {
        this.servedRegularCars = count;
    }

    /**
     * Sets the number of served electric cars.
     *
     * @param count the number of served electric cars
     */
    @Override
    public void setServedElectricCars(int count) {
        this.servedElectricCars = count;
    }

    /**
     * Sets the number of rejected customers.
     *
     * @param count the number of rejected customers
     */
    @Override
    public void setRejectedCustomers(int count) {
        this.rejectedCustomers = count;
    }
}