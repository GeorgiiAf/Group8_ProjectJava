package simu.model;

import dao.SimulationResultDao;
import eduni.distributions.ContinuousGenerator;
import entity.SimulationResult;
import simu.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

import java.util.*;
/**
 * Represents the main engine of the simulation.
 */
public class OmaMoottori extends Moottori{

	private final Saapumisprosessi saapumisprosessi;
	private final List<Asiakas> customers = new ArrayList<>();
	private final List<SimulationListener> listeners = new ArrayList<>();

	ArrayList<Palvelupiste> arrival = new ArrayList<>();
	ArrayList<Palvelupiste> diagnostics = new ArrayList<>();
	ArrayList<Palvelupiste> parts = new ArrayList<>();
	ArrayList<Palvelupiste> carReady = new ArrayList<>();
	ArrayList<Palvelupiste> simpleMaintenance = new ArrayList<>();

	ArrayList<ArrayList<Palvelupiste>> allServicePoints = new ArrayList<>();

	private double totalEarnings = 0.0;
	private int servedRegularCars = 0;
	private int servedElectricCars = 0;
	private int rejectedCustomers = 0;

	private int arrivalSpots = 10;
	private int diagnosticSpots = 15;
	private int partsSpots = 20;
	private int maintenanceSpots = 10;
	private int carReadySpots = 10;

	private boolean simulointiLoppu = false;
	private double simulointiaika;

	private int arrivalTime;
	private int diagnosticsTime;
	private int partsTime;
	private int maintenanceTime;
	private int readyTime;

	private double defaultCost = 150;
	private double electricCost = 300;
	private double partsCost = 75;
	/**
	 * Constructs a new OmaMoottori instance.
	 */
	public OmaMoottori(){

		saapumisprosessi = new Saapumisprosessi(new Negexp(3), tapahtumalista, TapahtumanTyyppi.CAR_ARRIVES);


	}

	/**
	 * Sets the spot values for different service points.
	 *
	 * @param arrivalSpots the number of arrival spots
	 * @param diagnosticSpots the number of diagnostic spots
	 * @param partsSpots the number of parts spots
	 * @param maintenanceSpots the number of maintenance spots
	 * @param carReadySpots the number of car ready spots
	 */
	public void setSpotValues(int arrivalSpots, int diagnosticSpots, int partsSpots, int maintenanceSpots, int carReadySpots){
		this.arrivalSpots = arrivalSpots;
		this.diagnosticSpots = diagnosticSpots;
		this.partsSpots = partsSpots;
		this.maintenanceSpots = maintenanceSpots;
		this.carReadySpots = carReadySpots;
	}

	/**
	 * Gets the list of all service points.
	 *
	 * @return the list of all service points
	 */
	public ArrayList<ArrayList<Palvelupiste>> getAllServicePointsList() {
		return allServicePoints;
	}


	/**
	 * Creates a list of service points.
	 *
	 * @param amount the number of service points
	 * @param mechanics the number of mechanics
	 * @param serviceTime the service time generator
	 * @param tapahtumanTyyppi the event type
	 * @return the list of service points
	 */
	private ArrayList<Palvelupiste> createPalvelupiste(int amount, int mechanics, ContinuousGenerator serviceTime, TapahtumanTyyppi tapahtumanTyyppi){
		ArrayList<Palvelupiste> servicePoints = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			servicePoints.add(new Palvelupiste(mechanics, serviceTime, tapahtumalista, tapahtumanTyyppi));
		}
		return servicePoints;
	}
	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava(new newCustomer(25.0, 10.0, this.defaultCost, this.electricCost, this.partsCost).buildCustomer());
		this.servedElectricCars = 0;
		this.servedRegularCars = 0;
		this.totalEarnings = 0.0;
		this.rejectedCustomers = 0;

		this.arrival.clear();
		this.diagnostics.clear();
		this.parts.clear();
		this.carReady.clear();
		this.simpleMaintenance.clear();

		this.arrival.addAll(createPalvelupiste(arrivalSpots, 10, new Normal(arrivalTime, (double) arrivalTime / 2), TapahtumanTyyppi.CAR_ARRIVES));
		this.diagnostics.addAll(createPalvelupiste(diagnosticSpots, 5, new Normal(diagnosticsTime, (double) diagnosticsTime / 2), TapahtumanTyyppi.DIAGNOSTIC_DONE));
		this.parts.addAll(createPalvelupiste(partsSpots, 5, new Normal(partsTime, (double) partsTime / 2), TapahtumanTyyppi.PARTS_ORDERED));
		this.simpleMaintenance.addAll(createPalvelupiste(maintenanceSpots, 10, new Normal(maintenanceTime, (double) maintenanceTime / 2), TapahtumanTyyppi.SIMPLE_MAINTENANCE));
		this.carReady.addAll(createPalvelupiste(carReadySpots, 5, new Normal(readyTime, (double) readyTime / 2), TapahtumanTyyppi.CAR_READY));

		this.allServicePoints.add(arrival);
		this.allServicePoints.add(diagnostics);
		this.allServicePoints.add(parts);
		this.allServicePoints.add(simpleMaintenance);
		this.allServicePoints.add(carReady);
		Asiakas.i = 0;
	}




	@Override
	protected void suoritaTapahtuma(Tapahtuma t) {
        if (simulointiLoppu) {
			tulokset();
			return;
		}
		switch ((TapahtumanTyyppi) t.getTyyppi()) {
			case CAR_ARRIVES:
				handleArrival(t);
				break;

			case DIAGNOSTIC_DONE:
				handleDiagnostics(t);
				break;

			case PARTS_ORDERED:
				handleParts(t);
				break;

			case SIMPLE_MAINTENANCE:
				handleMaintenance(t);
				break;

			case CAR_READY:
				Trace.out(Trace.Level.INFO, "CAR READY");
				handleCarReady(t);
				break;
		}

		if (isSimulationFinished()) {
			simulointiLoppu = true;

			String message = String.format(
                    """
                            [Simulation Finished] All customers processed.
                            Total earnings: %.2f EUR
                            Served regular cars: %d
                            Served electric cars: %d
                            Rejected customers: %d""",
					totalEarnings, servedRegularCars, servedElectricCars, rejectedCustomers
			);
			notifyListeners(message);
		}

	}

	private void handleArrival(Tapahtuma t) {
		Asiakas uusiAsiakas =
				new newCustomer(
	25.0,
		10.0,
					this.defaultCost,
					this.electricCost,
					this.partsCost
				)
					.buildCustomer();
		customers.add(uusiAsiakas);
		Palvelupiste q = shortestQueue(this.diagnostics);
		q.lisaaJonoon(uusiAsiakas);
		uusiAsiakas.setCurrentQueueIndex(this.diagnostics.indexOf(q));
		String message = String.format(
				"[Arrival] Customer %d (%s) arrived and added to diagnostics queue.",
				uusiAsiakas.getId(),
				uusiAsiakas.isElectricCar() ? "Electric Car" : "Regular Car"
		);
		notifyListeners(message);

		saapumisprosessi.generoiSeuraava(uusiAsiakas);
	}

	private void handleDiagnostics(Tapahtuma t) {
		Asiakas a = diagnostics.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();
		String message = String.format(
				"[Diagnostics] Customer %d diagnostics complete. Needs parts: %s.",
				a.getId(),
				!a.isNoPartsNeeded()
		);
		notifyListeners(message);
		Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " diagnostics complete. Needs parts: " + !a.isNoPartsNeeded());
		if (a.isNoPartsNeeded()) {

			Palvelupiste q = shortestQueue(this.simpleMaintenance);
			q.lisaaJonoon(a);
			a.setCurrentQueueIndex(this.simpleMaintenance.indexOf(q));
			Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " moved to SIMPLE_MAINTENANCE queue.");
			message = String.format(
					"[Diagnostics] Customer %d moved to SIMPLE_MAINTENANCE queue.",
					a.getId()
			);
			notifyListeners(message);
		} else {

			Palvelupiste q = shortestQueue(this.parts);
			q.lisaaJonoon(a);
			a.setCurrentQueueIndex(this.parts.indexOf(q));
			Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " moved to PARTS_ORDERED queue. Queue size: " + q.getQueueSize());
			message = String.format(
					"[Diagnostics] Customer %d moved to PARTS_ORDERED queue. Queue size: %d.",
					a.getId(),
					q.getQueueSize()
			);
			notifyListeners(message);
		}
	}

	private void handleParts(Tapahtuma t) {
		Asiakas c = this.parts.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();

		Palvelupiste q = shortestQueue(this.carReady);
		q.lisaaJonoon(c);

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " parts ordering complete.");
		String message = String.format(
				"[Parts] Customer %d parts ordering complete. Moved to CAR_READY queue. Queue size: %d.",
				c.getId(),
				q.getQueueSize()
		);
		notifyListeners(message);
		c.setCurrentQueueIndex(this.carReady.indexOf(q));

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " moved to CAR_READY queue. Queue size: " + q.getQueueSize());

	}

	private void handleMaintenance(Tapahtuma t) {
		Asiakas c = this.simpleMaintenance.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();

		Palvelupiste q = shortestQueue(this.carReady);
		q.lisaaJonoon(c);

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " simple maintenance complete.");
		String message = String.format(
				"[Maintenance] Customer %d maintenance complete. Moved to CAR_READY queue. Queue size: %d.",
				c.getId(),
				q.getQueueSize()
		);
		notifyListeners(message);
		c.setCurrentQueueIndex(this.carReady.indexOf(q));

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " moved to CAR_READY queue. Queue size: " + q.getQueueSize());

	}

	private void handleCarReady(Tapahtuma t) {
		Asiakas a = this.carReady.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();
		a.setPoistumisaika(Kello.getInstance().getAika());

		totalEarnings += a.calculateServiceCost();
		if (a.isElectricCar()) {
			servedElectricCars++;
		} else {
			servedRegularCars++;
		}
		String message = String.format(
				"[Car Ready] Customer %d (%s) service completed. Total earnings: %.2f EUR.",
				a.getId(),
				a.isElectricCar() ? "Electric Car" : "Regular Car",
				totalEarnings
		);
		String raporttiMsg = a.raportti();
		notifyListeners(raporttiMsg);
	}

	public Palvelupiste shortestQueue(ArrayList<Palvelupiste> servicePointArr) {
		if (servicePointArr.isEmpty()) {
			throw new IllegalStateException("No service points available in the list???");
		}
		Palvelupiste shortest = servicePointArr.getFirst();
		int minQueueSize = shortest.getQueueSize();

		for (int i = 1; i < servicePointArr.size(); i++) {
			Palvelupiste current = servicePointArr.get(i);
			int currentQueueSize = current.getQueueSize();
			if (currentQueueSize < minQueueSize) {
				shortest = current;
				minQueueSize = currentQueueSize;
			}
		}

		return shortest;
	}

	@Override
	protected void yritaCTapahtumat(){
		for (ArrayList<Palvelupiste> servicePointList : allServicePoints) {
			for (Palvelupiste p : servicePointList) {
				if (!p.onVarattu() && p.onJonossa()) {

					p.aloitaPalvelu();
				}
			}
		}
	}

	@Override
	public void tulokset() {
		simulointiLoppu = true;

		System.out.println("Simulointi päättyi kello " + Kello.getInstance().getAika());
		System.out.println("Total earnings: " + totalEarnings);
		System.out.println("Served regular cars: " + servedRegularCars);
		System.out.println("Served electric cars: " + servedElectricCars);

		int customersInQueue = 0;
		for (ArrayList<Palvelupiste> servicePoints : allServicePoints) {
			for (Palvelupiste p : servicePoints) {
				customersInQueue += p.getQueueSize();
			}
		}

		rejectedCustomers += customersInQueue;

		SimulationResult result = new SimulationResult(
				totalEarnings,
				servedRegularCars,
				servedElectricCars,
				rejectedCustomers
		);

		SimulationResultDao dao = new SimulationResultDao();
		dao.saveSimulationResult(result);

		String message = String.format(
				"""
                        [Simulation Finished] Simulation stopped at time %.2f.
                        Total earnings: %.2f EUR
                        Served regular cars: %d
                        Served electric cars: %d
                        Rejected customers: %d
                        Customers still in queue: %d""",
				Kello.getInstance().getAika(), totalEarnings, servedRegularCars, servedElectricCars, rejectedCustomers, customersInQueue
		);

		notifyListeners(message);

		System.out.println("Customers still in queue: " + customersInQueue);
	}

	/**
	 * Calculates the total earnings from the simulation.
	 *
	 * @return the total earnings
	 */
	public double calculateTotalEarnings() {
		return totalEarnings;
	}
	/**
	 * Gets the number of served regular cars.
	 *
	 * @return the number of served regular cars
	 */
	public int getServedRegularCars() {
		return servedRegularCars;
	}
	/**
	 * Gets the number of served electric cars.
	 *
	 * @return the number of served electric cars
	 */
	public int getServedElectricCars() {
		return servedElectricCars;
	}

	/**
	 * Gets the number of rejected customers.
	 *
	 * @return the number of rejected customers
	 */
	public int getRejectedCustomers() {
		return rejectedCustomers;
	}

	/**
	 * Sets the simulation end flag.
	 *
	 * @param lopeta true to end the simulation, false otherwise
	 */
	public void setSimulointiLoppu(boolean lopeta) {
		this.simulointiLoppu = lopeta;
	}


	/**
	 * Sets the simulation duration.
	 *
	 * @param aika the simulation time in seconds
	 * @throws IllegalArgumentException if the time is less than or equal to zero
	 */
	public void setSimulointiAika(double aika) {
		if (aika <= 0) {
			throw new IllegalArgumentException("Simulation time must be greater than 0");
		}
		this.simulointiaika = aika;
	}
	/**
	 * Sets the service times for all service points.
	 *
	 * @param arrival     the arrival time
	 * @param diagnostics the diagnostics time
	 * @param parts       the parts ordering time
	 * @param maintenance the maintenance time
	 * @param ready       the ready time
	 */
	public void setAllServiceTime(int arrival, int diagnostics, int parts, int maintenance, int ready){
		this.arrivalTime = arrival;
		this.diagnosticsTime = diagnostics;
		this.partsTime = parts;
		this.maintenanceTime = maintenance;
		this.readyTime = ready;
	}
	/**
	 * Sets the pricing for different services.
	 *
	 * @param defaultCost the default service cost
	 * @param electricCost the cost for electric cars
	 * @param partsCost the cost for parts
	 */
	public void setAllPrices(double defaultCost, double electricCost, double partsCost) {
		this.defaultCost = defaultCost;
		this.electricCost = electricCost;
		this.partsCost = partsCost;
	}

	/**
	 * Adds a simulation listener.
	 *
	 * @param listener the listener to add
	 */
	public void addSimulationListener(SimulationListener listener) {
		listeners.add(listener);
	}
	/**
	 * Notifies all listeners with a message.
	 *
	 * @param message the message to send
	 */
	private void notifyListeners(String message) {
		for (SimulationListener listener : listeners) {
			listener.onLogMessage(message);
		}
	}
	/**
	 * Checks if the simulation is finished.
	 *
	 * @return true if the simulation is finished, false otherwise
	 */
	private boolean isSimulationFinished() {
		for (ArrayList<Palvelupiste> servicePoints : allServicePoints) {
			for (Palvelupiste p : servicePoints) {
				if (p.getQueueSize() > 0 || p.onVarattu()) {
					Trace.out(Trace.Level.INFO, "Simulation not finished: Queue not empty or service point is busy.");
					return false;
				}
			}
		}
		for (Asiakas a : customers) {
			if (!a.isServiceCompleted()) {
				Trace.out(Trace.Level.INFO, "Simulation not finished: Customer " + a.getId() + " is not processed.");
				return false;
			}
		}

		Trace.out(Trace.Level.INFO, "Simulation finished: All customers processed.");
		return true;
	}

	/**
	 * Checks if the simulation has ended.
	 *
	 * @return true if the simulation has ended, false otherwise
	 */
	public boolean isSimulointiLoppu() {
		return simulointiLoppu;
	}
}
