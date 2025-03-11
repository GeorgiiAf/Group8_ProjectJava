package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a simulation result.
 */
@Entity
@Table(name = "simulation_results")
public class SimulationResult {

    /**
     * The unique identifier for the simulation result.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The total earnings from the simulation.
     */
    @Column(name = "total_earnings", nullable = false)
    private double totalEarnings;

    /**
     * The number of served regular cars in the simulation.
     */
    @Column(name = "served_regular_cars", nullable = false)
    private int servedRegularCars;

    /**
     * The number of served electric cars in the simulation.
     */
    @Column(name = "served_electric_cars", nullable = false)
    private int servedElectricCars;

    /**
     * The number of rejected customers in the simulation.
     */
    @Column(name = "rejected_customers", nullable = false)
    private int rejectedCustomers;

    /**
     * The time when the simulation was run.
     */
    @Column(name = "simulation_time", nullable = false)
    private LocalDateTime simulationTime;

    /**
     * Default constructor for JPA.
     */
    public SimulationResult() {
    }

    /**
     * Constructs a new SimulationResult with the specified details.
     *
     * @param totalEarnings the total earnings from the simulation
     * @param servedRegularCars the number of served regular cars
     * @param servedElectricCars the number of served electric cars
     * @param rejectedCustomers the number of rejected customers
     */
    public SimulationResult(double totalEarnings, int servedRegularCars, int servedElectricCars, int rejectedCustomers) {
        this.totalEarnings = totalEarnings;
        this.servedRegularCars = servedRegularCars;
        this.servedElectricCars = servedElectricCars;
        this.rejectedCustomers = rejectedCustomers;
        this.simulationTime = LocalDateTime.now();
    }

    // Getters and setters with Javadoc comments

    /**
     * Gets the unique identifier for the simulation result.
     *
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the simulation result.
     *
     * @param id the unique identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the total earnings from the simulation.
     *
     * @return the total earnings
     */
    public double getTotalEarnings() {
        return totalEarnings;
    }

    /**
     * Sets the total earnings from the simulation.
     *
     * @param totalEarnings the total earnings
     */
    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    /**
     * Gets the number of served regular cars in the simulation.
     *
     * @return the number of served regular cars
     */
    public int getServedRegularCars() {
        return servedRegularCars;
    }

    /**
     * Sets the number of served regular cars in the simulation.
     *
     * @param servedRegularCars the number of served regular cars
     */
    public void setServedRegularCars(int servedRegularCars) {
        this.servedRegularCars = servedRegularCars;
    }

    /**
     * Gets the number of served electric cars in the simulation.
     *
     * @return the number of served electric cars
     */
    public int getServedElectricCars() {
        return servedElectricCars;
    }

    /**
     * Sets the number of served electric cars in the simulation.
     *
     * @param servedElectricCars the number of served electric cars
     */
    public void setServedElectricCars(int servedElectricCars) {
        this.servedElectricCars = servedElectricCars;
    }

    /**
     * Gets the number of rejected customers in the simulation.
     *
     * @return the number of rejected customers
     */
    public int getRejectedCustomers() {
        return rejectedCustomers;
    }

    /**
     * Sets the number of rejected customers in the simulation.
     *
     * @param rejectedCustomers the number of rejected customers
     */
    public void setRejectedCustomers(int rejectedCustomers) {
        this.rejectedCustomers = rejectedCustomers;
    }

    /**
     * Gets the time when the simulation was run.
     *
     * @return the simulation time
     */
    public LocalDateTime getSimulationTime() {
        return simulationTime;
    }

    /**
     * Sets the time when the simulation was run.
     *
     * @param simulationTime the simulation time
     */
    public void setSimulationTime(LocalDateTime simulationTime) {
        this.simulationTime = simulationTime;
    }
}