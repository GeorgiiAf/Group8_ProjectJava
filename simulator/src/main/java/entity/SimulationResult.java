package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_results")
public class SimulationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_earnings", nullable = false)
    private double totalEarnings;

    @Column(name = "served_regular_cars", nullable = false)
    private int servedRegularCars;

    @Column(name = "served_electric_cars", nullable = false)
    private int servedElectricCars;

    @Column(name = "rejected_customers", nullable = false)
    private int rejectedCustomers;

    @Column(name = "simulation_time", nullable = false)
    private LocalDateTime simulationTime;

    public SimulationResult() {
    }

    public SimulationResult(double totalEarnings, int servedRegularCars, int servedElectricCars, int rejectedCustomers) {
        this.totalEarnings = totalEarnings;
        this.servedRegularCars = servedRegularCars;
        this.servedElectricCars = servedElectricCars;
        this.rejectedCustomers = rejectedCustomers;
        this.simulationTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public int getServedRegularCars() {
        return servedRegularCars;
    }

    public void setServedRegularCars(int servedRegularCars) {
        this.servedRegularCars = servedRegularCars;
    }

    public int getServedElectricCars() {
        return servedElectricCars;
    }

    public void setServedElectricCars(int servedElectricCars) {
        this.servedElectricCars = servedElectricCars;
    }

    public int getRejectedCustomers() {
        return rejectedCustomers;
    }

    public void setRejectedCustomers(int rejectedCustomers) {
        this.rejectedCustomers = rejectedCustomers;
    }

    public LocalDateTime getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(LocalDateTime simulationTime) {
        this.simulationTime = simulationTime;
    }
}