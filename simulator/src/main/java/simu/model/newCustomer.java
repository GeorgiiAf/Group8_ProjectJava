package simu.model;

import java.util.Random;

public class newCustomer {
    private final double electricCarCustomers;
    private final double noPartsNeeded;

    private final Random random;

    public newCustomer(double electricCarCustomers, double noPartsNeeded) {
        this.electricCarCustomers = electricCarCustomers;
        this.noPartsNeeded = noPartsNeeded;
        this.random = new Random();
    }

    public Asiakas buildCustomer() {
        boolean isElectricCar = this.random.nextDouble() < (this.electricCarCustomers / 100.0);
        boolean noPartsNeeded = this.random.nextDouble() < (this.noPartsNeeded / 100.0);

        return new Asiakas(isElectricCar, noPartsNeeded);
    }
}
