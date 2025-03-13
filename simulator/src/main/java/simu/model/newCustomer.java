package simu.model;

import java.util.Random;

/**
 * Represents a builder for creating new customer instances.
 */
public class newCustomer {
    private final double electricCarCustomers;
    private final double noPartsNeeded;

    private final Random random;

    private double defaultCost;
    private double electricCost;
    private double partsCost;

    /**
     * Constructs a new customer builder.
     *
     * @param electricCarCustomers the percentage of electric car customers
     * @param noPartsNeeded the percentage of customers needing no parts
     * @param defaultCost the default cost of service
     * @param electricCost the cost for electric cars
     * @param partsCost the cost for parts
     */
    public newCustomer(
            double electricCarCustomers,
            double noPartsNeeded,
            double defaultCost,
            double electricCost,
            double partsCost
    ) {
        this.electricCarCustomers = electricCarCustomers;
        this.noPartsNeeded = noPartsNeeded;
        this.random = new Random();
        this.defaultCost = defaultCost;
        this.electricCost = electricCost;
        this.partsCost = partsCost;
    }

    /**
     * Builds a new customer instance.
     *
     * @return the new customer instance
     */
    public Asiakas buildCustomer() {
        boolean isElectricCar = this.random.nextDouble() < (this.electricCarCustomers / 100.0);
        boolean noPartsNeeded = this.random.nextDouble() < (this.noPartsNeeded / 100.0);

        return new Asiakas(isElectricCar, noPartsNeeded, defaultCost, electricCost, partsCost);
    }
}