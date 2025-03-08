package simu.model;

import java.util.Random;

public class newCustomer {
    private final double electricCarCustomers;
    private final double noPartsNeeded;

    private final Random random;

    private double defaultCost;
    private double electricCost;
    private double partsCost;

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
    }

    public Asiakas buildCustomer() {
        boolean isElectricCar = this.random.nextDouble() < (this.electricCarCustomers / 100.0);
        boolean noPartsNeeded = this.random.nextDouble() < (this.noPartsNeeded / 100.0);

        return new Asiakas(isElectricCar, noPartsNeeded, defaultCost, electricCost, partsCost);
    }
}
