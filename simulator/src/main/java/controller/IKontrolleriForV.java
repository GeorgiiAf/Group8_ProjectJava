package controller;

/**
 * Interface for controlling the simulation.
 */
public interface IKontrolleriForV {

    /**
     * Gets the total earnings from the simulation.
     *
     * @return the total earnings
     */
    double getTotalEarnings();

    /**
     * Gets the number of served regular cars.
     *
     * @return the number of served regular cars
     */
    int getServedRegularCars();

    /**
     * Gets the number of served electric cars.
     *
     * @return the number of served electric cars
     */
    int getServedElectricCars();

    /**
     * Gets the number of rejected customers.
     *
     * @return the number of rejected customers
     */
    int getRejectedCustomers();

    /**
     * Sets the total earnings for the simulation.
     *
     * @param earnings the total earnings
     */
    void setTotalEarnings(double earnings);

    /**
     * Sets the number of served regular cars.
     *
     * @param count the number of served regular cars
     */
    void setServedRegularCars(int count);

    /**
     * Sets the number of served electric cars.
     *
     * @param count the number of served electric cars
     */
    void setServedElectricCars(int count);

    /**
     * Sets the number of rejected customers.
     *
     * @param count the number of rejected customers
     */
    void setRejectedCustomers(int count);

    /**
     * Starts the simulation.
     */
    void kaynnistaSimulointi();

    /**
     * Speeds up the simulation.
     */
    void nopeuta();

    /**
     * Slows down the simulation.
     */
    void hidasta();
}