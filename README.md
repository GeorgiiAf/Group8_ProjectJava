# Car Workshop Simulator

Welcome to the **Car Workshop Simulator**! This simulator models the operations of a car workshop, where cars go through a series of service points to get repaired or maintained. The simulator is designed to help you understand the workflow of a car workshop, optimize resource allocation, and analyze the efficiency of the service process.

---

## Overview

The simulator consists of **5 service points**, each representing a stage in the car repair process:

1. **Car Arrival**: Cars arrive at the workshop and are added to the diagnostics queue.
2. **Diagnostic Done**: Cars are diagnosed to determine if they need parts or simple maintenance.
3. **Parts Ordered**: If parts are needed, they are ordered, and the car waits in this queue.
4. **Simple Maintenance**: If no parts are needed, the car undergoes simple maintenance.
5. **Car Ready**: After diagnostics, parts ordering, or maintenance, the car is marked as ready and leaves the workshop.

The simulator tracks key metrics such as:
- Total earnings
- Number of regular and electric cars served
- Number of rejected customers
- Time spent in each service point

---

## Features

- **Customizable Service Points**: Adjust the number of spots available at each service point (e.g., arrival spots, diagnostic spots, etc.).
- **Dynamic Queue Management**: Cars are routed to the shortest queue at each service point to optimize efficiency.
- **Cost Calculation**: Different costs are applied for regular cars, electric cars, and parts ordering.
- **Simulation Logging**: Detailed logs are generated for each step of the simulation, providing insights into the workflow.
- **Simulation Results**: At the end of the simulation, a summary of results is displayed, including total earnings, cars served, and rejected customers.

---

## How It Works

The simulator uses a discrete-event simulation model to simulate the workflow of the car workshop. Here's a high-level overview of the process:

1. **Car Arrival**: Cars arrive at the workshop based on a random distribution (e.g., exponential distribution for arrival times).
2. **Diagnostics**: Cars are diagnosed to determine if they need parts or simple maintenance.
3. **Parts Ordering**: If parts are needed, the car is moved to the parts queue.
4. **Simple Maintenance**: If no parts are needed, the car is moved to the maintenance queue.
5. **Car Ready**: After completing the required service, the car is marked as ready and leaves the workshop.

---

## Key Classes and Methods

### `OmaMoottori` (Main Simulation Engine)
- **Responsibilities**:
    - Manages the simulation process.
    - Handles car arrivals, diagnostics, parts ordering, maintenance, and car readiness.
    - Tracks simulation metrics (earnings, cars served, etc.).
- **Key Methods**:
    - `alustukset()`: Initializes the simulation by setting up service points and queues.
    - `suoritaTapahtuma(Tapahtuma t)`: Processes events based on their type (e.g., car arrival, diagnostics done).
    - `tulokset()`: Displays simulation results at the end.

### `Palvelupiste` (Service Point)
- **Responsibilities**:
    - Represents a service point in the workshop (e.g., diagnostics, parts ordering).
    - Manages the queue of cars waiting for service.
    - Tracks the status of the service point (busy or idle).

### `Asiakas` (Customer/Car)
- **Responsibilities**:
    - Represents a car in the simulation.
    - Tracks the car's type (regular or electric), service requirements, and service completion status.

### `Saapumisprosessi` (Arrival Process)
- **Responsibilities**:
    - Generates car arrivals based on a random distribution.

---

## Configuration

You can customize the simulation by adjusting the following parameters:

- **Service Point Spots**:
    - `arrivalSpots`: Number of spots in the arrival area.
    - `diagnosticSpots`: Number of diagnostic spots.
    - `partsSpots`: Number of parts ordering spots.
    - `maintenanceSpots`: Number of maintenance spots.
    - `carReadySpots`: Number of spots in the car-ready area.

- **Service Times**:
    - `arrivalTime`: Time spent in the arrival area.
    - `diagnosticsTime`: Time spent in diagnostics.
    - `partsTime`: Time spent ordering parts.
    - `maintenanceTime`: Time spent in maintenance.
    - `readyTime`: Time spent in the car-ready area.

- **Costs**:
    - `defaultCost`: Base cost for regular cars.
    - `electricCost`: Additional cost for electric cars.
    - `partsCost`: Cost for parts ordering.

---

## Example Output

At the end of the simulation, you will see a summary like this:
[Simulation Finished] All customers processed.

Total earnings: 5250.00 EUR

Served regular cars: 20

Served electric cars: 10

Rejected customers: 5


---

## Dependencies

The simulator uses the following libraries:
- `eduni.distributions`: For generating random numbers based on statistical distributions (e.g., exponential, normal).
- Custom framework classes: For managing events, queues, and simulation logic.

---

## Future Enhancements

- **GUI**: Make GUI more easy to use.
- **Advanced Metrics**: Track additional metrics such as average wait time, service point utilization, and customer satisfaction.
- **Dynamic Pricing**: Implement dynamic pricing based on demand and workshop capacity.

---

Enjoy simulating and optimizing your car workshop! If you have any questions or suggestions, please reach out.

Happy simulating! 🚗🔧