package simu.framework;
// REWORK THIS
public class SimulationResults {
    private final double totalEarnings;
    private final int servedRegularCars;
    private final int servedElectricCars;
    private final int rejectedCustomers;

    public SimulationResults(double totalEarnings, int servedRegularCars, int servedElectricCars, int rejectedCustomers) {
        this.totalEarnings = totalEarnings;
        this.servedRegularCars = servedRegularCars;
        this.servedElectricCars = servedElectricCars;
        this.rejectedCustomers = rejectedCustomers;
    }

    public double getTotalEarnings() { return totalEarnings; }
    public int getServedRegularCars() { return servedRegularCars; }
    public int getServedElectricCars() { return servedElectricCars; }
    public int getRejectedCustomers() { return rejectedCustomers; }
}